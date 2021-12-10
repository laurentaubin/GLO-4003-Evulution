package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchEmailAlreadyInUseExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.CONFLICT.getStatusCode();
  private static final String EXPECTED_ERROR = "REGISTER_FAILED";
  private static final String EXPECTED_DESCRIPTION = "The email address is already is use.";

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
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
