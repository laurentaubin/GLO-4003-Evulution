package ca.ulaval.glo4003.ws.api.shared.exception;

public class InvalidFormatException extends RuntimeException {
  private final String description;

  public InvalidFormatException(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
