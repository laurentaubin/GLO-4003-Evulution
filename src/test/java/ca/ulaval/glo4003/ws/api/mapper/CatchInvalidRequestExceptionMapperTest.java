package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidRequestExceptionMapperTest {
  private static final String A_PROPERTY = "Property";

  private InvalidRequestExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new InvalidRequestExceptionMapper();
  }

  @Test
  void givenInvalidFormatException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidFormatException exception = new InvalidFormatException(A_PROPERTY);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
    assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
  }
}
