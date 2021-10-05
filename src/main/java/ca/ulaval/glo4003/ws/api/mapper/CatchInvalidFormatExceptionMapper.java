package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_FORMAT";

  @Override
  public Response toResponse(InvalidFormatException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, exception.getDescription()))
        .build();
  }
}
