package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CatchUserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {
  private static final int STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();

  @Override
  public Response toResponse(UserNotFoundException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(exception.getError(), exception.getDescription()))
        .build();
  }
}
