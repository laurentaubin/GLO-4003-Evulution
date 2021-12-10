package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

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
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).matches(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).matches(EXPECTED_DESCRIPTION);
  }
}
