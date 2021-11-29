package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public class OwnershipHandler {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final UserRepository userRepository;

  public OwnershipHandler() {
    this(serviceLocator.resolve(UserRepository.class));
  }

  public OwnershipHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void mapDeliveryIdToTransactionId(
      Session session, TransactionId transactionId, DeliveryId deliveryId) {
    User user = userRepository.findUser(session.getEmail());
    user.addTransactionDelivery(transactionId, deliveryId);
    userRepository.update(user);
  }

  public void validateTransactionOwnership(Session session, TransactionId transactionId) {
    User user = userRepository.findUser(session.getEmail());
    if (!user.ownsTransaction(transactionId)) {
      throw new WrongOwnerException();
    }
  }

  public void validateDeliveryOwnership(Session session, DeliveryId deliveryId) {
    User user = userRepository.findUser(session.getEmail());
    if (!user.doesOwnDelivery(deliveryId)) {
      throw new WrongOwnerException();
    }
  }

  public TransactionId retrieveTransactionId(Session session, DeliveryId deliveryId) {
    User user = userRepository.findUser(session.getEmail());
    return user.getTransactionIdFromDeliveryId(deliveryId);
  }
}
