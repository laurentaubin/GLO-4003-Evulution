package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchWrongOwnerExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();
  private static final String EXPECTED_ERROR = "FORBIDDEN";
  private static final String EXPECTED_DESCRIPTION = "You are not the owner of this transaction.";

  private CatchWrongOwnerExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchWrongOwnerExceptionMapper();
  }

  @Test
  public void givenWrongOwnerException_whenToResponse_thenReturnRightResponse() {
    // given
    WrongOwnerException exception = new WrongOwnerException();

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
