package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.exception.InvalidBatteryException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

class CatchInvalidBatteryExceptionMapperTest {
  private static final Set<String> BATTERY_TYPES = Set.of("SHORT_RANGE", "LONG_RANGE");
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_BATTERY_TYPE";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Battery must be one of the following type: {%s}.", BATTERY_TYPES);

  private CatchInvalidBatteryExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidBatteryExceptionMapper();
  }

  @Test
  void givenInvalidBatteryException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidBatteryException exception = new InvalidBatteryException(BATTERY_TYPES);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
