package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.user.exception.BirthDateInTheFutureException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchBirthDateInTheFutureExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_FORMAT";
  private static final String EXPECTED_DESCRIPTION = "The birth date entered is in the future.";

  private CatchBirthDateInTheFutureExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchBirthDateInTheFutureExceptionMapper();
  }

  @Test
  public void givenBirthDateInTheFutureException_whenToResponse_thenReturnRightResponse() {
    // given
    BirthDateInTheFutureException exception = new BirthDateInTheFutureException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
