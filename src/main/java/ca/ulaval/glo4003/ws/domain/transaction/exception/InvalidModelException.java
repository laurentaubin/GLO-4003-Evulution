package ca.ulaval.glo4003.ws.domain.transaction.exception;

public class InvalidModelException extends RuntimeException {

  public static String error;
  public static String description;

  public InvalidModelException() {
    this.error = "INVALID_MODEL";
    this.description = "model must be of type Vandry, Pouliot or Desjardins";
  }
}
