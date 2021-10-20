package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.shared.exception.EmptyTokenHeaderException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchEmptyTokenHeaderExceptionMapper
    implements ExceptionMapper<EmptyTokenHeaderException> {
  private static final int STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();
  private static final String ERROR = "EMPTY_TOKEN";
  private static final String DESCRIPTION = "Authorization header is empty.";

  @Override
  public Response toResponse(EmptyTokenHeaderException e) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
