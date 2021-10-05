package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidLocationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidLocationExceptionMapper
    implements ExceptionMapper<InvalidLocationException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_CAMPUS_LOCATION";
  private static final String DESCRIPTION = "Location must be one of the following building: {%s}.";

  @Override
  public Response toResponse(InvalidLocationException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, exception.getLocations())))
        .build();
  }
}
