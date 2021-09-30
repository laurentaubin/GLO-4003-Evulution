package ca.ulaval.glo4003.ws.domain.transaction.exception;

public class InvalidBankAccountException extends RuntimeException {
  public static String error;
  public static String description;

  public InvalidBankAccountException(String description) {
    this.error = "INVALID_BANK_ACCOUNT";
    this.description = description;
  }
}
