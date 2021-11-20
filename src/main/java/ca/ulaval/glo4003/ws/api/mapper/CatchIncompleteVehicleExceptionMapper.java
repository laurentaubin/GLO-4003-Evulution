package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.vehicle.exception.IncompleteVehicleException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchIncompleteVehicleExceptionMapper
    implements ExceptionMapper<IncompleteVehicleException> {

  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR_MESSAGE = "INVALID_FORMAT";
  private static final String ERROR_DESCRIPTION =
      "Cannot calculate the price of an incomplete vehicle.";

  @Override
  public Response toResponse(IncompleteVehicleException exception) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR_MESSAGE, ERROR_DESCRIPTION))
        .build();
  }
}
