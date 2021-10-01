package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.ModelNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidModelExceptionMapperTest {
  private CatchModelNotFoundExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchModelNotFoundExceptionMapper();
  }

  @Test
  void givenInvalidModelException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    ModelNotFoundException exception = new ModelNotFoundException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(ModelNotFoundException.error, exceptionResponse.getError());
    assertEquals(ModelNotFoundException.description, exceptionResponse.getDescription());
  }
}
