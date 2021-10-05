package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchCannotAddBatteryBeforeVehicleExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "CANNOT_ADD_BATTERY_BEFORE_VEHICLE";
  private static final String EXPECTED_DESCRIPTION =
      "A vehicle must be added to the transaction before adding a battery.";

  private CatchCannotAddBatteryBeforeVehicleExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchCannotAddBatteryBeforeVehicleExceptionMapper();
  }

  @Test
  public void
      givenCannotAddBatteryBeforeVehicleException_whenToResponse_thenReturnCorrectResponse() {
    // given
    CannotAddBatteryBeforeVehicleException exception = new CannotAddBatteryBeforeVehicleException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(EXPECTED_STATUS_CODE, response.getStatus());
    assertEquals(EXPECTED_ERROR, exceptionResponse.getError());
    assertEquals(EXPECTED_DESCRIPTION, exceptionResponse.getDescription());
  }
}
