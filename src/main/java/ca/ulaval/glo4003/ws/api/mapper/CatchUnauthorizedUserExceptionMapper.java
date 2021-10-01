package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CatchUnauthorizedUserExceptionMapper
    implements ExceptionMapper<UnauthorizedUserException> {
  private static final int STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();

  @Override
  public Response toResponse(UnauthorizedUserException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(exception.getError(), exception.getDescription()))
        .build();
  }
}
