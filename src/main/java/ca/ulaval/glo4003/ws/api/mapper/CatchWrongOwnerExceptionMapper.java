package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.user.exception.WrongOwnerException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchWrongOwnerExceptionMapper implements ExceptionMapper<WrongOwnerException> {
  private static final int STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();
  private static final String ERROR = "FORBIDDEN";

  @Override
  public Response toResponse(WrongOwnerException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, exception.getMessage()))
        .build();
  }
}
