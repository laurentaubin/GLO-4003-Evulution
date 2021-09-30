package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidModelException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidModelExceptionMapperTest {
  private InvalidModelExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new InvalidModelExceptionMapper();
  }

  @Test
  void givenInvalidModelException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidModelException exception = new InvalidModelException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
