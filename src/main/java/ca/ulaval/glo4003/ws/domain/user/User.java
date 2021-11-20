package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.exception.NoTransactionLinkedToDeliveryException;

import java.util.*;

public class User {
  private final String name;
  private final BirthDate birthDate;
  private final String sex;
  private final String email;
  private final String password;
  private final Set<Role> roles;
  private Map<TransactionId, DeliveryId> transactionIdToDeliveryId;

  public User(String name, BirthDate birthDate, String sex, String email, String password) {
    this.name = name;
    this.birthDate = birthDate;
    this.sex = sex;
    this.email = email;
    this.password = password;
    this.roles = new HashSet<>(List.of(Role.BASE));
    this.transactionIdToDeliveryId = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public BirthDate getBirthDate() {
    return birthDate;
  }

  public String getSex() {
    return sex;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void addRole(Role role) {
    this.roles.add(role);
  }

  public void setTransactionIdToDeliveryId(
      Map<TransactionId, DeliveryId> transactionIdToDeliveryId) {
    this.transactionIdToDeliveryId = new HashMap<>(transactionIdToDeliveryId);
  }

  public Set<Role> getRoles() {
    return this.roles;
  }

  public boolean isAllowed(List<Role> requestedRoles) {
    return !Collections.disjoint(roles, requestedRoles);
  }

  public void addTransactionDelivery(TransactionId transactionId, DeliveryId deliveryId) {
    if (transactionId != null && deliveryId != null) {
      transactionIdToDeliveryId.put(transactionId, deliveryId);
    }
  }

  public TransactionId getTransactionIdFromDeliveryId(DeliveryId deliveryId) {
    Optional<TransactionId> transactionId =
        transactionIdToDeliveryId.keySet().stream()
            .filter(txId -> transactionIdToDeliveryId.get(txId).equals(deliveryId))
            .findFirst();
    if (transactionId.isPresent()) {
      return transactionId.get();
    }

    throw new NoTransactionLinkedToDeliveryException(deliveryId);
  }

  public boolean doesOwnTransaction(TransactionId transactionId) {
    return transactionIdToDeliveryId.containsKey(transactionId);
  }

  public boolean doesOwnDelivery(DeliveryId deliveryId) {
    return transactionIdToDeliveryId.containsValue(deliveryId);
  }

  public Map<TransactionId, DeliveryId> getTransactionIdToDeliveryId() {
    return transactionIdToDeliveryId;
  }
}
