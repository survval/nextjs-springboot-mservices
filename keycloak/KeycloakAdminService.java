package com.example.keycloak.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakAdminService {

  private final Keycloak kc;

  /* ---------- REALM ---------- */
  public void ensureRealm(String realm) {
    boolean exists = kc.realms().findAll().stream()
        .anyMatch(r -> r.getRealm().equals(realm));
    if (!exists) {
      RealmRepresentation rep = new RealmRepresentation();
      rep.setRealm(realm);
      rep.setEnabled(true);
      kc.realms().create(rep);
      log.info("Realm '{}' created", realm);
    }
  }

  /* ---------- CLIENT ---------- */
  public void ensureClient(String realm, String clientId, String secret) {
    var realmRes = kc.realm(realm);
    Optional<ClientRepresentation> opt = realmRes.clients()
        .findByClientId(clientId).stream().findFirst();
    if (opt.isEmpty()) {
      ClientRepresentation c = new ClientRepresentation();
      c.setClientId(clientId);
      c.setSecret(secret);
      c.setBearerOnly(true);
      Response r = realmRes.clients().create(c);
      log.info("Client '{}' created → {}", clientId, CreatedResponseUtil.getCreatedId(r));
    }
  }

  /* ---------- ROLE ---------- */
  public void ensureRole(String realm, String role) {
    var realmRes = kc.realm(realm);
    if (realmRes.roles().list().stream().noneMatch(r -> r.getName().equals(role))) {
      realmRes.roles().create(new RoleRepresentation(role, "auto role", false));
      log.info("Role '{}' created", role);
    }
  }

  /* ---------- USER ---------- */
  public void ensureUser(String realm, String username, String password, String role) {
    var realmRes = kc.realm(realm);
    if (realmRes.users().search(username).isEmpty()) {
      UserRepresentation u = new UserRepresentation();
      u.setUsername(username);
      u.setEnabled(true);
      String id = CreatedResponseUtil.getCreatedId(realmRes.users().create(u));

      CredentialRepresentation pw = new CredentialRepresentation();
      pw.setType(CredentialRepresentation.PASSWORD);
      pw.setValue(password);
      pw.setTemporary(false);
      realmRes.users().get(id).resetPassword(pw);

      RoleRepresentation rr = realmRes.roles().get(role).toRepresentation();
      realmRes.users().get(id).roles().realmLevel().add(List.of(rr));
      log.info("User '{}' created with role '{}'", username, role);
    }
  }
}
