package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class CatchDuplicateDeliveryExceptionMapperTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");
  private static final int EXPECTED_STATUS_CODE =
      Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  private static final String EXPECTED_ERROR = "DUPLICATE_DELIVERY";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Delivery with id %s already exists.", AN_ID);

  private CatchDuplicateDeliveryExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchDuplicateDeliveryExceptionMapper();
  }

  @Test
  void givenDuplicateDeliveryException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    DuplicateDeliveryException exception = new DuplicateDeliveryException(AN_ID);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
