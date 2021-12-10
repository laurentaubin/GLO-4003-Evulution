package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class CatchUnauthorizedUserExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();
  private static final String EXPECTED_ERROR = "UNAUTHORIZED_USER";
  private static final String EXPECTED_DESCRIPTION =
      "This user does not have the permissions to perform this action.";

  private CatchUnauthorizedUserExceptionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CatchUnauthorizedUserExceptionMapper();
  }

  @Test
  public void givenUnauthorizedUserException_whenToResponse_thenReturnRightResponse() {
    // given
    UnauthorizedUserException exception = new UnauthorizedUserException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
