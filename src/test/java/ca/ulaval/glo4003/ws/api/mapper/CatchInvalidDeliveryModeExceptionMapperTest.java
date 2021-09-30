package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidDeliveryModeException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidDeliveryModeExceptionMapperTest {
  private CatchInvalidDeliveryModeExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidDeliveryModeExceptionMapper();
  }

  @Test
  void givenInvalidDeliveryModeException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidDeliveryModeException exception = new InvalidDeliveryModeException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
