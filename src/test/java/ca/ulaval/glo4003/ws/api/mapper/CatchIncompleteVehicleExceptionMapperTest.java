package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.vehicle.exception.IncompleteVehicleException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchIncompleteVehicleExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_FORMAT";
  private static final String EXPECTED_DESCRIPTION =
      "Cannot calculate the price of an incomplete vehicle.";

  private CatchIncompleteVehicleExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchIncompleteVehicleExceptionMapper();
  }

  @Test
  public void givenIncompleteVehicleException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    IncompleteVehicleException exception =
        new IncompleteVehicleException("Vehicle must be complete to calculate price.");

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
