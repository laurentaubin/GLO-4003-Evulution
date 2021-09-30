package ca.ulaval.glo4003.ws.api.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.InvalidFrequencyException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidFrequencyExceptionMapperTest {
  private CatchInvalidFrequencyExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidFrequencyExceptionMapper();
  }

  @Test
  void givenInvalidFrequencyException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidFrequencyException exception = new InvalidFrequencyException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
