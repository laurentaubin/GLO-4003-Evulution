package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.Role;
import java.util.Collection;
import java.util.Set;

public class UserDto {
  private final String name;
  private final BirthDate birthDate;
  private final String sex;
  private final String email;
  private final String password;
  private final Set<Role> roles;
  private final Collection<TransactionId> transactions;
  private final Collection<DeliveryId> deliveries;

  public UserDto(
      String name,
      BirthDate birthDate,
      String sex,
      String email,
      String password,
      Set<Role> roles,
      Collection<TransactionId> transactions,
      Collection<DeliveryId> deliveries) {
    this.name = name;
    this.birthDate = birthDate;
    this.sex = sex;
    this.email = email;
    this.password = password;
    this.roles = roles;
    this.transactions = transactions;
    this.deliveries = deliveries;
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

  public Collection<TransactionId> getTransactions() {
    return transactions;
  }

  public Collection<DeliveryId> getDeliveries() {
    return deliveries;
  }
}
