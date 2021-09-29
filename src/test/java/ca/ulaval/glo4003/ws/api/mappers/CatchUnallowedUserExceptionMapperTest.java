package ca.ulaval.glo4003.ws.api.mappers;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.exception.UnallowedUserException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchUnallowedUserExceptionMapperTest {
  private CatchUnallowedUserExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchUnallowedUserExceptionMapper();
  }

  @Test
  void givenUnallowedUserException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    UnallowedUserException exception = new UnallowedUserException();

    // when
    Response response = exceptionMapper.toResponse(exception);

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
  }
}
