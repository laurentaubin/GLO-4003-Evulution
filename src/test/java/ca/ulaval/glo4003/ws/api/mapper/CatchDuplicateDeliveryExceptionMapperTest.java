package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchDuplicateDeliveryExceptionMapperTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");

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
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
