package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.user.exception.BirthDateInTheFutureException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchBirthDateInTheFutureExceptionMapper
    implements ExceptionMapper<BirthDateInTheFutureException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR_MESSAGE = "INVALID_FORMAT";
  private static final String ERROR_DESCRIPTION = "The birth date entered is in the future.";

  @Override
  public Response toResponse(BirthDateInTheFutureException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR_MESSAGE, ERROR_DESCRIPTION))
        .build();
  }
}
