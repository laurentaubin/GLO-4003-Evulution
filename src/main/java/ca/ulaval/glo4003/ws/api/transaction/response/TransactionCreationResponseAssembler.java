package ca.ulaval.glo4003.ws.api.transaction.response;

import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;

public class TransactionCreationResponseAssembler {

  public TransactionCreationResponse assemble(TransactionCreationDto transactionCreationDto) {
    TransactionCreationResponse TransactionCreationResponse = new TransactionCreationResponse();
    TransactionCreationResponse.transactionId = transactionCreationDto.getTransactionId();
    TransactionCreationResponse.deliveryId = transactionCreationDto.getDeliveryId();
    return TransactionCreationResponse;
  }
}
