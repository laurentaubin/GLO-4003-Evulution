package ca.ulaval.glo4003.ws.api.mappers;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.exception.LoginFailedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchLoginFailedMapperTest {
  private CatchLoginFailedMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchLoginFailedMapper();
  }

  @Test
  void givenLoginFailedException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    LoginFailedException exception = new LoginFailedException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    assertThat(exceptionResponse.getError()).isEqualTo("LOGIN_FAILED");
    assertThat(exceptionResponse.getDescription())
        .isEqualTo("The email or password entered was invalid");
  }
}
