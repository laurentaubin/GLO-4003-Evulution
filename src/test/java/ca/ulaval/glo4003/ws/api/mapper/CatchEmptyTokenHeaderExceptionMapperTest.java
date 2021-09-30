package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.util.exception.EmptyTokenHeaderException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchEmptyTokenHeaderExceptionMapperTest {
  private CatchEmptyTokenHeaderExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchEmptyTokenHeaderExceptionMapper();
  }

  @Test
  void givenEmptyTokenHeaderException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    EmptyTokenHeaderException exception = new EmptyTokenHeaderException();

    // when
    Response response = exceptionMapper.toResponse(exception);

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
  }
}
