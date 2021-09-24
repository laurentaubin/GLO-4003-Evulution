package ca.ulaval.glo4003.ws.api.mappers;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.customer.exception.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidRequestFormatMapperTest {
  private CatchInvalidRequestFormatMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidRequestFormatMapper();
  }

  @Test
  void givenInvalidFormatException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidFormatException exception = new InvalidFormatException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    assertThat(exceptionResponse.getError()).isEqualTo(exception.getError());
    assertThat(exceptionResponse.getDescription()).isEqualTo(exception.getDescription());
  }
}
