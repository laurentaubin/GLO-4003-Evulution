package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.CreatedTransactionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class CreatedTransactionResponseAssembler {

  public CreatedTransactionResponse create(Transaction transaction) {
    CreatedTransactionResponse createdTransactionResponse = new CreatedTransactionResponse();
    createdTransactionResponse.transactionId = transaction.getId().toString();
    return createdTransactionResponse;
  }
}