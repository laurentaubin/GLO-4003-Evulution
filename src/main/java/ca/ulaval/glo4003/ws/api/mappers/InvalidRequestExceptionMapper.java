package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.shared.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidRequestExceptionMapper implements ExceptionMapper<InvalidFormatException> {

  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();

  @Override
  public Response toResponse(InvalidFormatException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(exception.error, exception.description))
        .build();
  }
}
