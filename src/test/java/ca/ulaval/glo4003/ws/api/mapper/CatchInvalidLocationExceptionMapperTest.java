package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidLocationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidLocationExceptionMapperTest {
  private CatchInvalidLocationExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidLocationExceptionMapper();
  }

  @Test
  void givenEmptyTokenHeaderException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidLocationException exception = new InvalidLocationException();

    // when
    Response response = exceptionMapper.toResponse(exception);

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
  }
}
