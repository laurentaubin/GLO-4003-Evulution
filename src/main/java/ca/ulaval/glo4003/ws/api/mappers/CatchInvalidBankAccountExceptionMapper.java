package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.InvalidBankAccountException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidBankAccountExceptionMapper
    implements ExceptionMapper<InvalidBankAccountException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();

  @Override
  public Response toResponse(InvalidBankAccountException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(e.error, e.description))
        .build();
  }
}