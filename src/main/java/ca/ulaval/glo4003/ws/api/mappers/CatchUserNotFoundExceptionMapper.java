package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.exception.UnallowedUserException;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CatchUserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {
  private static final int STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();

  @Override
  public Response toResponse(UserNotFoundException e) {
    return Response.status(STATUS_CODE)
        .entity(
            new ExceptionResponse(
                UnallowedUserException.getError(), UnallowedUserException.getDescription()))
        .build();
  }
}
