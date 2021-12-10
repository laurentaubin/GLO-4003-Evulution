package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;

public class OwnershipDomainService {
  public void validateTransactionOwnership(User user, TransactionId transactionId) {
    if (!user.ownsTransaction(transactionId)) {
      throw new TransactionNotFoundException(transactionId);
    }
  }

  public void validateDeliveryOwnership(User user, DeliveryId deliveryId) {
    if (!user.ownDelivery(deliveryId)) {
      throw new DeliveryNotFoundException(deliveryId);
    }
  }
}
