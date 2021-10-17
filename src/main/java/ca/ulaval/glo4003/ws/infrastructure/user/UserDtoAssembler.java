package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserDtoAssembler {

  public UserDto assemble(User user) {
    Set<Role> rolesCopy = new HashSet<>(user.getRoles());
    Collection<TransactionId> transactionsCopy = new ArrayList<>(user.getTransactions());
    Collection<DeliveryId> deliveriesCopy = new ArrayList<>(user.getDeliveries());

    return new UserDto(
        user.getName(),
        user.getBirthDate(),
        user.getSex(),
        user.getEmail(),
        user.getPassword(),
        rolesCopy,
        transactionsCopy,
        deliveriesCopy);
  }

  public User assemble(UserDto userDto) {
    User user =
        new User(
            userDto.getName(),
            userDto.getBirthDate(),
            userDto.getSex(),
            userDto.getEmail(),
            userDto.getPassword());

    for (Role role : userDto.getRoles()) {
      user.addRole(role);
    }

    for (TransactionId transactionId : userDto.getTransactions()) {
      user.addTransaction(transactionId);
    }

    for (DeliveryId deliveryId : userDto.getDeliveries()) {
      user.addDelivery(deliveryId);
    }

    return user;
  }
}
