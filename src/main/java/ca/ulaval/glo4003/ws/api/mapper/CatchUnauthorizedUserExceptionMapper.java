package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchUnauthorizedUserExceptionMapper
    implements ExceptionMapper<UnauthorizedUserException> {
  private static final int STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();
  private static final String ERROR = "UNAUTHORIZED_USER";
  private static final String DESCRIPTION =
      "This user does not have the permissions to perform this action.";

  @Override
  public Response toResponse(UnauthorizedUserException exception) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
