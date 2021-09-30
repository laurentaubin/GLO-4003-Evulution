package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchDeliveryNotFoundExceptionMapperTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");

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
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
