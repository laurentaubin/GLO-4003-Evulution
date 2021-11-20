package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotReadyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchDeliveryNotReadyExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "DELIVERY_NOT_READY";
  private static final String EXPECTED_DESCRIPTION = "The requested delivery is not ready";

  private CatchDeliveryNotReadyExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchDeliveryNotReadyExceptionMapper();
  }

  @Test
  void givenDeliveryNotReadyException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    DeliveryNotReadyException exception = new DeliveryNotReadyException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).matches(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).matches(EXPECTED_DESCRIPTION);
  }
}
