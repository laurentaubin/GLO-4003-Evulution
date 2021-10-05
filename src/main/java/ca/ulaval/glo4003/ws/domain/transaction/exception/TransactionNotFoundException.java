package ca.ulaval.glo4003.ws.domain.transaction.exception;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public class TransactionNotFoundException extends RuntimeException {
  private final TransactionId transactionId;

  public TransactionNotFoundException(TransactionId transactionId) {
    this.transactionId = transactionId;
  }

  public TransactionId getTransactionId() {
    return transactionId;
  }
}
