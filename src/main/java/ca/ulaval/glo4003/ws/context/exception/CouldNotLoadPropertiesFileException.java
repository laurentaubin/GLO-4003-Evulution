package ca.ulaval.glo4003.ws.context.exception;

public class CouldNotLoadPropertiesFileException extends RuntimeException {
  public CouldNotLoadPropertiesFileException(Exception exception) {
    super(exception);
  }
}
