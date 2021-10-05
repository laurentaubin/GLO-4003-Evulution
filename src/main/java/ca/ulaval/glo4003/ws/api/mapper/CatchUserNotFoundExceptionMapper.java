package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchUserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {
  private static final int STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();
  private static final String ERROR = "USER_NOT_FOUND";
  private static final String DESCRIPTION = "This user does not exist.";

  @Override
  public Response toResponse(UserNotFoundException exception) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
