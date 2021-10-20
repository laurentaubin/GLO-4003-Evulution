package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.exception.InvalidBatteryException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidBatteryExceptionMapper
    implements ExceptionMapper<InvalidBatteryException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_BATTERY_TYPE";
  private static final String DESCRIPTION = "Battery must be one of the following type: {%s}.";

  @Override
  public Response toResponse(InvalidBatteryException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getBatteryTypes())))
        .build();
  }
}
