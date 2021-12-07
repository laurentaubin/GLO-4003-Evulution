package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;

public class TransactionCreationDtoAssembler {
  public TransactionCreationDto assemble(TransactionId transactionId, DeliveryId deliveryId) {
    return new TransactionCreationDto(transactionId.getId(), deliveryId.getDeliveryId());
  }
}
