package ca.ulaval.glo4003.ws.domain.delivery;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.exception.WrongDeliveryOwnerException;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;

public class DeliveryOwnershipHandler {
  private final UserRepository userRepository;

  public DeliveryOwnershipHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void addDeliveryOwnership(Session session, DeliveryId deliveryId) {
    User user = userRepository.findUser(session.getEmail()).get();
    user.addDelivery(deliveryId);
    userRepository.update(user);
  }

  public void validateOwnership(Session session, DeliveryId deliveryId) {
    User user = userRepository.findUser(session.getEmail()).get();

    if (!user.doesOwnDelivery(deliveryId)) {
      throw new WrongDeliveryOwnerException();
    }
  }
}
