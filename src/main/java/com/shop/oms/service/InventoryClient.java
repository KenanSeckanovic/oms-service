package com.shop.oms.service;

import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import inventory.OrderRequest;
import inventory.OrderResponse;
import io.grpc.ManagedChannel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryClient {

    private final InventoryServiceBlockingStub stub;

    public InventoryClient(ManagedChannel channel) {
        this.stub = InventoryServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Reserviert für alle Items den Bestand (PlaceOrder).
     * Wenn eines fehlschlägt, werden bereits reservierte Bestände per ReleaseStock zurückgegeben.
     */
    public boolean reserveItems(List<com.shop.oms.dto.CreateOrderRequest.Item> items) {
        List<com.shop.oms.dto.CreateOrderRequest.Item> reservedSoFar = new ArrayList<>();

        for (var i : items) {
            OrderRequest req = OrderRequest.newBuilder()
                    // Mapping: wir nutzen productId als productName (falls ihr echte Namen habt -> hier anpassen)
                    .setProductName(i.productId())
                    .setQuantity(i.quantity())
                    .build();

            OrderResponse res = stub.placeOrder(req);
            if (res == null || !res.getSuccess()) {
                // Rollback
                for (var r : reservedSoFar) {
                    try {
                        stub.releaseStock(OrderRequest.newBuilder()
                                .setProductName(r.productId())
                                .setQuantity(r.quantity())
                                .build());
                    } catch (Exception ignored) { }
                }
                return false;
            }
            reservedSoFar.add(i);
        }
        return true;
    }

    /**
     * Gibt Bestände wieder frei – z.B. bei fehlgeschlagener Zahlung.
     */
    public void releaseItems(List<com.shop.oms.dto.CreateOrderRequest.Item> items) {
        for (var i : items) {
            try {
                stub.releaseStock(OrderRequest.newBuilder()
                        .setProductName(i.productId())
                        .setQuantity(i.quantity())
                        .build());
            } catch (Exception ignored) { }
        }
    }
}
