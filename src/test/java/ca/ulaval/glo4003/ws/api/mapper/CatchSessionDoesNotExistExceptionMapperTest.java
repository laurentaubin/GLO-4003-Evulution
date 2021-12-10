package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatchSessionDoesNotExistExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_TOKEN";
  private static final String EXPECTED_DESCRIPTION = "Invalid authorization token.";

  private CatchSessionDoesNotExistExceptionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CatchSessionDoesNotExistExceptionMapper();
  }

  @Test
  public void givenSessionDoesNotExistException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    SessionDoesNotExistException exception = new SessionDoesNotExistException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
