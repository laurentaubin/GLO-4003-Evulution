package ca.ulaval.glo4003.ws.domain.user.exception;

public class WrongTransactionOwnerException extends WrongOwnerException {
  private static final String DESCRIPTION = "You are not the owner of this transaction";

  public WrongTransactionOwnerException() {
    super(DESCRIPTION);
  }
}
