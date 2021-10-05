package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchCannotAddBatteryBeforeVehicleExceptionMapper
    implements ExceptionMapper<CannotAddBatteryBeforeVehicleException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "CANNOT_ADD_BATTERY_BEFORE_VEHICLE";
  private static final String DESCRIPTION =
      "A vehicle must be added to the transaction before adding a battery.";

  @Override
  public Response toResponse(CannotAddBatteryBeforeVehicleException exception) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
