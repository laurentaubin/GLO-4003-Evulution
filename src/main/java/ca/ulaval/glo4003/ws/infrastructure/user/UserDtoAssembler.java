package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserDtoAssembler {

  public UserDto assemble(User user) {
    Set<Role> rolesCopy = new HashSet<>(user.getRoles());
    Map<TransactionId, DeliveryId> transactionDeliveries =
        new HashMap<>(user.getTransactionIdToDeliveryId());

    return new UserDto(
        user.getName(),
        user.getBirthDate(),
        user.getSex(),
        user.getEmail(),
        rolesCopy,
        transactionDeliveries);
  }

  public User assemble(UserDto userDto) {
    User user =
        new User(userDto.getName(), userDto.getBirthDate(), userDto.getSex(), userDto.getEmail());

    for (Role role : userDto.getRoles()) {
      user.addRole(role);
    }

    user.setTransactionIdToDeliveryId(userDto.getTransactionDeliveries());
    return user;
  }
}
