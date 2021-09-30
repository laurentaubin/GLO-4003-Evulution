package ca.ulaval.glo4003.ws.domain.transaction.exception;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public class DuplicateTransactionException extends RuntimeException {

  public static String error;
  public static String description;

  public DuplicateTransactionException(TransactionId transactionId) {
    this.error = "DUPLICATE_TRANSACTION";
    this.description = String.format("transaction with id %s already in repository", transactionId);
  }
}
