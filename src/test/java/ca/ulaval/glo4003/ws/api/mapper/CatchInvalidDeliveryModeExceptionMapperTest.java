package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidDeliveryModeException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

class CatchInvalidDeliveryModeExceptionMapperTest {
  private static final Set<String> A_DELIVERY_MODE = Set.of("At campus");
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_DELIVERY_MODE";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Delivery mode must be one of the following mode: {%s}.", A_DELIVERY_MODE);

  private CatchInvalidDeliveryModeExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidDeliveryModeExceptionMapper();
  }

  @Test
  void givenInvalidDeliveryModeException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidDeliveryModeException exception = new InvalidDeliveryModeException(A_DELIVERY_MODE);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
