package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchSessionDoesNotExistExceptionMapperTest {
  private CatchSessionDoesNotExistExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchSessionDoesNotExistExceptionMapper();
  }

  @Test
  void givenSessionNotFoundException_whenToResponse_thenResponseHasRightStatusCode() {
    // given
    SessionDoesNotExistException exception = new SessionDoesNotExistException();

    // when
    Response response = exceptionMapper.toResponse(exception);

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
  }
}
