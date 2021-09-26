package ca.ulaval.glo4003.ws.api.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DuplicateTransactionExceptionMapperTest {
  private static final TransactionId AN_ID = new TransactionId("id");

  private DuplicateTransactionExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new DuplicateTransactionExceptionMapper();
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
