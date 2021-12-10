package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class CatchDuplicateTransactionExceptionMapperTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final int EXPECTED_STATUS_CODE =
      Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  private static final String EXPECTED_ERROR = "DUPLICATE_TRANSACTION";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Transaction with id %s already in repository.", AN_ID);

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
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
