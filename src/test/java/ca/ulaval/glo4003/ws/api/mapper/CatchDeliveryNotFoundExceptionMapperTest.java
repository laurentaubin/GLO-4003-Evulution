package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchDeliveryNotFoundExceptionMapperTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");
  private static final int EXPECTED_STATUS_CODE = Status.NOT_FOUND.getStatusCode();
  private static final String EXPECTED_ERROR = "DELIVERY_NOT_FOUND";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Could not find delivery with id %s.", AN_ID);

  private CatchDeliveryNotFoundExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchDeliveryNotFoundExceptionMapper();
  }

  @Test
  void givenDeliveryNotFoundException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    DeliveryNotFoundException exception = new DeliveryNotFoundException(AN_ID);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).matches(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).matches(EXPECTED_DESCRIPTION);
  }
}
