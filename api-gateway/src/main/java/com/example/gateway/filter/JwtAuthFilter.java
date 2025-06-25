package com.example.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter that:
 *  • Looks for an Authorization: Bearer <token> header.
 *  • Decodes & validates JWT using Spring’s JwtDecoder (which auto-loads
 *    Keycloak’s JWKs from issuer-uri in application.yml).
 *  • Rejects bad / missing tokens with 401.
 *
 * Downstream resource servers (e.g., product-service) still do their own
 * @PreAuthorize role checks, but this stops noise at the edge.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

  private final JwtDecoder jwtDecoder;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    // Public endpoints or OPTIONS pre-flights: simply continue.
    if (header == null || !header.startsWith("Bearer ")) {
      return chain.filter(exchange);
    }

    String token = header.substring(7);

    try {
      jwtDecoder.decode(token);       // signature, exp, nbf validations
      return chain.filter(exchange);  // token is OK → continue routing
    } catch (JwtException ex) {
      log.warn("JWT validation failed: {}", ex.getMessage());
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
  }

  /** Run early, but after Netty routing = -1 is OK */
  @Override
  public int getOrder() {
    return -1;
  }
}
