package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.exception.UnallowedUserException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CatchUnallowedUserExceptionMapper implements ExceptionMapper<UnallowedUserException> {
  private static final int STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();

  @Override
  public Response toResponse(UnallowedUserException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(exception.getError(), exception.getDescription()))
        .build();
  }
}
