package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidBankAccountException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidBankAccountExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_PAYMENT_OPTION";
  private static final String EXPECTED_DESCRIPTION =
      "Bank number must be 3 digits and account number must be 7 digits.";

  private CatchInvalidBankAccountExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidBankAccountExceptionMapper();
  }

  @Test
  void givenInvalidBankAccountException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidBankAccountException exception = new InvalidBankAccountException();

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
