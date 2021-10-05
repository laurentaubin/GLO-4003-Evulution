package ca.ulaval.glo4003.ws.domain.transaction.exception;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public class DuplicateTransactionException extends RuntimeException {
  private final TransactionId transactionId;

  public DuplicateTransactionException(TransactionId transactionId) {
    this.transactionId = transactionId;
  }

  public TransactionId getTransactionId() {
    return transactionId;
  }
}
