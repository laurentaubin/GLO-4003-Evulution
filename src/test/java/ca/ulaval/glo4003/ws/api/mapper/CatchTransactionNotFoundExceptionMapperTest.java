package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchTransactionNotFoundExceptionMapperTest {
  private static final TransactionId AN_ID = new TransactionId("id");

  private CatchTransactionNotFoundExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchTransactionNotFoundExceptionMapper();
  }

  @Test
  void givenTransactionNotFoundException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    TransactionNotFoundException exception = new TransactionNotFoundException(AN_ID);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
