package com.shop.oms.service;

import com.shop.oms.domain.Order;
import com.shop.oms.domain.OrderItem;
import com.shop.oms.domain.OrderStatus;
import com.shop.oms.dto.CreateOrderRequest;
import com.shop.oms.dto.CreateOrderResponse;
import com.shop.oms.dto.PaymentRequest;
import com.shop.oms.repo.InMemoryOrderRepository;
import com.shop.oms.service.SendNestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final InMemoryOrderRepository repo;
    private final InventoryClient inventory;
    private final PaymentClient payment;
    private final SendNestMessage wms;

    public OrderService(InMemoryOrderRepository repo, InventoryClient inventory, PaymentClient payment, SendNestMessage wms) {
        this.repo = repo;
        this.inventory = inventory;
        this.payment = payment;
        this.wms = wms;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest req) {
        // 1) Minimal "persistieren"
        Order order = Mapper.toOrder(req);
        order.setStatus(OrderStatus.RECEIVED);
        repo.save(order);
        log.info("Order received: {}", order.getOrderId());

        // 2) Inventory reservieren
        boolean reserved = inventory.reserveItems(req.items());
        if (!reserved) {
            order.setStatus(OrderStatus.CANCELLED);
            repo.save(order);
            log.info("Inventory not available → order cancelled: {}", order.getOrderId());
            return new CreateOrderResponse(order.getOrderId(), order.getStatus(), "Out of stock");
        }
        order.setStatus(OrderStatus.RESERVED);
        repo.save(order);
        log.info("Inventory reserved for order {}", order.getOrderId());

        // 3) Payment (Kundenname für Payment-Service zusammensetzen)
        String customerName = ((order.getPrename() == null ? "" : order.getPrename()) + " " +
                               (order.getName() == null ? "" : order.getName()))
                               .trim().replaceAll(" +", " ");

        var payRes = payment.charge(
            new PaymentRequest(order.getOrderId(), order.getTotalAmount(), "CARD"),
            customerName
        );

        if (payRes == null || !payRes.isSuccess()) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            repo.save(order);
            log.warn("Payment failed → releasing inventory for order {}", order.getOrderId());
            try { inventory.releaseItems(req.items()); } catch (Exception ignored) {}
            return new CreateOrderResponse(order.getOrderId(), order.getStatus(), "Payment failed");
        }
        order.setStatus(OrderStatus.PAYMENT_OK);
        repo.save(order);
        log.info("Payment ok for order {}", order.getOrderId());

        // 4) WMS: Fulfillment-Befehl senden
        try{
            wms.sendMessage("order_created", "Bestellung mit der Nummer" + order.getOrderId() + "bestätigt" );
        } 
        catch(Exception ex) {
            log.warn("Konnte sich nicht mit RabbitMq verbinden");
        }
        order.setStatus(OrderStatus.SENT_TO_WMS);
        repo.save(order);
        log.info("Sent to WMS: {}", order.getOrderId());

        return new CreateOrderResponse(order.getOrderId(), order.getStatus(), "Order sent to WMS");
    }

    // einfache Mapper-Helper
    static class Mapper {
        static Order toOrder(CreateOrderRequest r) {
            var o = new Order();
            o.setOrderId(r.orderId());
            o.setCustomerId(r.customer().customerId());
            o.setPrename(r.customer().prename());
            o.setName(r.customer().name());
            o.setItems(r.items().stream()
                    .map(i -> new OrderItem(i.productId(), i.quantity(), i.price()))
                    .toList());
            o.setTotalAmount(r.totalAmount());
            o.setStreet(r.shippingAddress().street());
            o.setCity(r.shippingAddress().city());
            o.setZipCode(r.shippingAddress().zipCode());
            o.setCountry(r.shippingAddress().country());
            return o;
        }
    }
}
