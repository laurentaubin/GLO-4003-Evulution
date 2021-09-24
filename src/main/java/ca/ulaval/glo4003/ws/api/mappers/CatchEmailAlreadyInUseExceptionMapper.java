package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.customer.exception.EmailAlreadyInUseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchEmailAlreadyInUseExceptionMapper
    implements ExceptionMapper<EmailAlreadyInUseException> {
  private static final int STATUS_CODE = Response.Status.CONFLICT.getStatusCode();
  private static final String ERROR_MESSAGE = "REGISTER_FAILED";
  private static final String ERROR_DESCRIPTION = "The email address is already is use";

  @Override
  public Response toResponse(EmailAlreadyInUseException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR_MESSAGE, ERROR_DESCRIPTION))
        .build();
  }
}
