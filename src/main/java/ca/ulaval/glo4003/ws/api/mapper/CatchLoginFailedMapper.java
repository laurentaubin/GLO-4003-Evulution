package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchLoginFailedMapper implements ExceptionMapper<LoginFailedException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR_MESSAGE = "LOGIN_FAILED";
  private static final String ERROR_DESCRIPTION = "The email or password entered was invalid.";

  @Override
  public Response toResponse(LoginFailedException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR_MESSAGE, ERROR_DESCRIPTION))
        .build();
  }
}
