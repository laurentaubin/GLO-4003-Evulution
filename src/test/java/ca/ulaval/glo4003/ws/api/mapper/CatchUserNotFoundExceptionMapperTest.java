package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class CatchUserNotFoundExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();
  private static final String EXPECTED_ERROR = "USER_NOT_FOUND";
  private static final String EXPECTED_DESCRIPTION = "This user does not exist.";

  private CatchUserNotFoundExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchUserNotFoundExceptionMapper();
  }

  @Test
  public void givenUserNotFoundException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    UserNotFoundException exception = new UserNotFoundException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
