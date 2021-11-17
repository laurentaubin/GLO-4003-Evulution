package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.Role;

import java.util.Map;
import java.util.Set;

public class UserDto {
  private final String name;
  private final BirthDate birthDate;
  private final String sex;
  private final String email;
  private final String password;
  private final Set<Role> roles;
  private final Map<TransactionId, DeliveryId> transactionDeliveries;

  public UserDto(
      String name,
      BirthDate birthDate,
      String sex,
      String email,
      String password,
      Set<Role> roles,
      Map<TransactionId, DeliveryId> transactionDeliveries) {
    this.name = name;
    this.birthDate = birthDate;
    this.sex = sex;
    this.email = email;
    this.password = password;
    this.roles = roles;
    this.transactionDeliveries = transactionDeliveries;
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

  public Set<Role> getRoles() {
    return roles;
  }

  public Map<TransactionId, DeliveryId> getTransactionDeliveries() {
    return transactionDeliveries;
  }
}
