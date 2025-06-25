
package com.example.gateway.filter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter {
  private final JwtDecoder decoder;
  public JwtAuthFilter(JwtDecoder decoder){this.decoder=decoder;}
  @Override
  public Mono<Void> filter(ServerWebExchange ex, GatewayFilterChain chain){
    String header=ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if(header==null||!header.startsWith("Bearer ")) return chain.filter(ex);
    try{decoder.decode(header.substring(7)); return chain.filter(ex);}
    catch(JwtException e){ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); return ex.getResponse().setComplete();}
  }
}
