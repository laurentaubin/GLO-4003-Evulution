package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.domain.exception.EmptyTokenHeaderException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CatchEmptyTokenHeaderExceptionMapper
    implements ExceptionMapper<EmptyTokenHeaderException> {

  private static final int STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();

  @Override
  public Response toResponse(EmptyTokenHeaderException e) {
    return Response.status(STATUS_CODE).build();
  }
}
