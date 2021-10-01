package ca.ulaval.glo4003.ws.domain.delivery.exception;

import ca.ulaval.glo4003.ws.domain.user.exception.WrongOwnerException;

public class WrongDeliveryOwnerException extends WrongOwnerException {
  private static final String DESCRIPTION = "You are not the owner of this delivery.";

  public WrongDeliveryOwnerException() {
    super(DESCRIPTION);
  }
}
