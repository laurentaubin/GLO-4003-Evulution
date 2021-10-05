package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidBankAccountException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidBankAccountExceptionMapper
    implements ExceptionMapper<InvalidBankAccountException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_PAYMENT_OPTION";
  private static final String DESCRIPTION =
      "Bank number must be 3 digits et account number must be 7 digits.";

  @Override
  public Response toResponse(InvalidBankAccountException e) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
