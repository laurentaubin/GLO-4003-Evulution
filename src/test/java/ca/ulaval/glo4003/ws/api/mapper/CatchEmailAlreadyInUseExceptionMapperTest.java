package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchEmailAlreadyInUseExceptionMapperTest {
  private CatchEmailAlreadyInUseExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchEmailAlreadyInUseExceptionMapper();
  }

  @Test
  void givenEmailAlreadyInUseException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    EmailAlreadyInUseException exception = new EmailAlreadyInUseException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.CONFLICT.getStatusCode());
    assertThat(exceptionResponse.getError()).isEqualTo("REGISTER_FAILED");
    assertThat(exceptionResponse.getDescription()).isEqualTo("The email address is already is use");
  }
}
