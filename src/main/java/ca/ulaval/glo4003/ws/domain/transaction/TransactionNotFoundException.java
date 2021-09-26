package ca.ulaval.glo4003.ws.domain.transaction;

public class TransactionNotFoundException extends RuntimeException {

  public static String error;
  public static String description;

  public TransactionNotFoundException(TransactionId transactionId) {
    this.error = "TRANSACTION_NOT_FOUND";
    this.description = String.format("Could not find transaction id %s", transactionId);
  }
}
