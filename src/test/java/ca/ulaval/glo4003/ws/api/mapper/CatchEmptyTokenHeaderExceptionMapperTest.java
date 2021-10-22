package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.shared.exception.EmptyTokenHeaderException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchEmptyTokenHeaderExceptionMapperTest {
  private CatchEmptyTokenHeaderExceptionMapper exceptionMapper;

  private static final int EXPECTED_STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();
  private static final String EXPECTED_ERROR = "EMPTY_TOKEN";
  private static final String EXPECTED_DESCRIPTION = "Authorization header is empty.";

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchEmptyTokenHeaderExceptionMapper();
  }

  @Test
  void givenEmptyTokenHeaderException_whenToResponse_thenReturnRightResponse() {
    // given
    EmptyTokenHeaderException exception = new EmptyTokenHeaderException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
