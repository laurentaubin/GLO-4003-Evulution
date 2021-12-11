package ca.ulaval.glo4003.ws.domain.user.exception;

public class InvalidDateFormatException extends RuntimeException {
  private final String description;

  public InvalidDateFormatException(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
