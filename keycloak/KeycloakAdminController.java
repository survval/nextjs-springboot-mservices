package com.example.keycloak.controller;

import com.example.keycloak.service.KeycloakAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/kc")
@RequiredArgsConstructor
public class KeycloakAdminController {

  private final KeycloakAdminService svc;

  /* 1. create / ensure realm */
  @PostMapping("/realms/{realm}")
  @ResponseStatus(HttpStatus.CREATED)
  public void realm(@PathVariable String realm) {
    svc.ensureRealm(realm);
  }

  /* 2. create / ensure client */
  @PostMapping("/realms/{realm}/clients")
  @ResponseStatus(HttpStatus.CREATED)
  public void client(@PathVariable String realm,
                     @RequestParam String clientId,
                     @RequestParam String secret) {
    svc.ensureClient(realm, clientId, secret);
  }

  /* 3. create / ensure role */
  @PostMapping("/realms/{realm}/roles/{role}")
  @ResponseStatus(HttpStatus.CREATED)
  public void role(@PathVariable String realm,
                   @PathVariable String role) {
    svc.ensureRole(realm, role);
  }

  /* 4. create / ensure user */
  @PostMapping("/realms/{realm}/users")
  @ResponseStatus(HttpStatus.CREATED)
  public void user(@PathVariable String realm,
                   @RequestParam String username,
                   @RequestParam String password,
                   @RequestParam String role) {
    svc.ensureUser(realm, username, password, role);
  }
}
