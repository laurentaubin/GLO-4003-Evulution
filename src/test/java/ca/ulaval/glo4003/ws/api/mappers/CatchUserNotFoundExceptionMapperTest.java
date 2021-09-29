package ca.ulaval.glo4003.ws.api.mappers;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchUserNotFoundExceptionMapperTest {
  private CatchUserNotFoundExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchUserNotFoundExceptionMapper();
  }

  @Test
  void givenUserFoundException_whenToResponse_thenResponseHasRightStatusCode() {
    // given
    UserNotFoundException exception = new UserNotFoundException();

    // when
    Response response = exceptionMapper.toResponse(exception);

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
  }
}
