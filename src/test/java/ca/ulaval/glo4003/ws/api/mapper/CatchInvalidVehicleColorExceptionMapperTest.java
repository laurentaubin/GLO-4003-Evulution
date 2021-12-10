package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.vehicle.exception.InvalidVehicleColorException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidVehicleColorExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_VEHICLE_COLOR";
  private static final String EXPECTED_DESCRIPTION = "Vehicle must be white";

  private CatchInvalidVehicleColorExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchInvalidVehicleColorExceptionMapper();
  }

  @Test
  public void givenInvalidVehicleColorException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidVehicleColorException exception = new InvalidVehicleColorException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
