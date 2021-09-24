package ca.ulaval.glo4003.ws.api.mappers;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.customer.exception.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidRequestFormatMapper implements ExceptionMapper<InvalidFormatException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();

  @Override
  public Response toResponse(InvalidFormatException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(exception.getError(), exception.getDescription()))
        .build();
  }
}
