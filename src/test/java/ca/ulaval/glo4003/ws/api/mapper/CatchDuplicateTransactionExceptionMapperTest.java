package ca.ulaval.glo4003.ws.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchDuplicateTransactionExceptionMapperTest {
  private static final TransactionId AN_ID = new TransactionId("id");

  private CatchDuplicateTransactionExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchDuplicateTransactionExceptionMapper();
  }

  @Test
  void givenDuplicateTransactionException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    DuplicateTransactionException exception = new DuplicateTransactionException(AN_ID);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertEquals(exception.error, exceptionResponse.getError());
    assertEquals(exception.description, exceptionResponse.getDescription());
  }
}
