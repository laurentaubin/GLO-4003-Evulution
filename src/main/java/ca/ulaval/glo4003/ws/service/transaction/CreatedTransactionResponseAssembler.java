package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.service.transaction.dto.CreatedTransactionResponse;

public class CreatedTransactionResponseAssembler {

  public CreatedTransactionResponse assemble(Transaction transaction, Delivery delivery) {
    CreatedTransactionResponse createdTransactionResponse = new CreatedTransactionResponse();
    createdTransactionResponse.transactionId = transaction.getId().toString();
    createdTransactionResponse.deliveryId = delivery.getDeliveryId().toString();
    return createdTransactionResponse;
  }
}
