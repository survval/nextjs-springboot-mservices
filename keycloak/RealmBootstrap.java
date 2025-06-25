package com.example.provisioner.bootstrap;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealmBootstrap {

  /* ---------- injected from application.yml / env ---------- */
  @Value("${kc.server-url}") String server;
  @Value("${kc.admin-username}") String adminU;
  @Value("${kc.admin-password}") String adminP;
  @Value("${kc.realm}") String realmName;
  @Value("${kc.client-id}") String clientId;
  @Value("${kc.client-secret}") String clientSecret;
  @Value("${kc.role}") String roleName;
  @Value("${kc.demo-user}") String demoUser;
  @Value("${kc.demo-pass}") String demoPass;

  @PostConstruct
  void provisionRealm() {
    Keycloak master = KeycloakBuilder.builder()
        .serverUrl(server)
        .realm("master")
        .clientId("admin-cli")
        .username(adminU)
        .password(adminP)
        .build();

    /* 1 ➜ realm */
    if (master.realms().findAll().stream().noneMatch(r -> r.getRealm().equals(realmName))) {
      RealmRepresentation rr = new RealmRepresentation();
      rr.setRealm(realmName);
      rr.setEnabled(true);
      master.realms().create(rr);
      log.info("Realm '{}' created", realmName);
    } else {
      log.info("Realm '{}' already exists", realmName);
    }

    var realm = master.realm(realmName);

    /* 2 ➜ client */
    Optional<ClientRepresentation> clientOpt =
        realm.clients().findByClientId(clientId).stream().findFirst();
    String clientUuid;
    if (clientOpt.isEmpty()) {
      ClientRepresentation c = new ClientRepresentation();
      c.setClientId(clientId);
      c.setSecret(clientSecret);
      c.setBearerOnly(true);
      c.setDirectAccessGrantsEnabled(false);
      c.setPublicClient(false);
      Response resp = realm.clients().create(c);
      clientUuid = CreatedResponseUtil.getCreatedId(resp);
      log.info("Client '{}' created", clientId);
    } else {
      clientUuid = clientOpt.get().getId();
      log.info("Client '{}' exists", clientId);
    }

    /* 3 ➜ role */
    boolean roleExists =
        realm.roles().list().stream().anyMatch(r -> r.getName().equals(roleName));
    if (!roleExists) {
      realm.roles().create(new RoleRepresentation(roleName, "auto role", false));
      log.info("Role '{}' created", roleName);
    } else {
      log.info("Role '{}' exists", roleName);
    }

    /* 4 ➜ demo user */
    if (realm.users().search(demoUser).isEmpty()) {
      UserRepresentation u = new UserRepresentation();
      u.setUsername(demoUser);
      u.setEnabled(true);
      String userId = CreatedResponseUtil.getCreatedId(realm.users().create(u));

      CredentialRepresentation pw = new CredentialRepresentation();
      pw.setType(CredentialRepresentation.PASSWORD);
      pw.setValue(demoPass);
      pw.setTemporary(false);
      realm.users().get(userId).resetPassword(pw);

      RoleRepresentation rr = realm.roles().get(roleName).toRepresentation();
      realm.users().get(userId).roles().realmLevel().add(List.of(rr));

      log.info("User '{}' created and given role '{}'", demoUser, roleName);
    } else {
      log.info("User '{}' already exists", demoUser);
    }

    log.info("Keycloak provisioning ✅ done");
    // let Spring shut down — container exits cleanly
  }
}
