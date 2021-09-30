package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CatchSessionDoesNotExistExceptionMapper
    implements ExceptionMapper<SessionDoesNotExistException> {
  private static final int STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();
  private static final String ERROR = "INVALID_TOKEN";
  private static final String DESCRIPTION = "Invalid token";

  @Override
  public Response toResponse(SessionDoesNotExistException e) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
