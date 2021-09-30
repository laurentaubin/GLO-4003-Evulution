package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.validator.exception.UnauthorizedUserException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchUnauthorizedUserExceptionMapperTest {
  private CatchUnauthorizedUserExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchUnauthorizedUserExceptionMapper();
  }

  @Test
  void givenUnallowedUserException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    UnauthorizedUserException exception = new UnauthorizedUserException();

    // when
    Response response = exceptionMapper.toResponse(exception);

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
  }
}
