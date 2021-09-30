package ca.ulaval.glo4003.ws.api.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.InvalidBankAccountException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidBankAccountExceptionMapperTest {
  private CatchInvalidBankAccountExceptionMapper exceptionMapper;
  private static final String INVALID_BANK_ACCOUNT_MESSAGE =
      "Bank number must contain three digits.";

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidBankAccountExceptionMapper();
  }

  @Test
  void givenInvalidBankAccountException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidBankAccountException exception =
        new InvalidBankAccountException(INVALID_BANK_ACCOUNT_MESSAGE);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
