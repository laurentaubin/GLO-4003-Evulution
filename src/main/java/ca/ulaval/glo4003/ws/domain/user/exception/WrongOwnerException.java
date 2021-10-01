package ca.ulaval.glo4003.ws.domain.user.exception;

public class WrongOwnerException extends RuntimeException {
  public WrongOwnerException(String description) {
    super(description);
  }
}
