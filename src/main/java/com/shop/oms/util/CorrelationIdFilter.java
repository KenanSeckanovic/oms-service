package com.shop.oms.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter implements Filter {
  public static final String CORRELATION_ID = "X-Correlation-Id";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest http = (HttpServletRequest) req;
    String cid = http.getHeader(CORRELATION_ID);
    if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
    MDC.put("cid", cid);
    try { chain.doFilter(req, res); }
    finally { MDC.remove("cid"); }
  }
}
