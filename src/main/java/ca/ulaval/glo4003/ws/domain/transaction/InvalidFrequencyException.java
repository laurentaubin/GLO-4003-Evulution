package ca.ulaval.glo4003.ws.domain.transaction;

public class InvalidFrequencyException extends RuntimeException {
  public static String error;
  public static String description;

  public InvalidFrequencyException() {
    this.error = "INVALID_FREQUENCY";
    this.description = "Frequency must be of type monthly, biweekly or weekly.";
  }
}
