package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class CatchTransactionNotFoundExceptionMapperTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final int EXPECTED_STATUS_CODE = Response.Status.NOT_FOUND.getStatusCode();
  private static final String EXPECTED_ERROR = "TRANSACTION_NOT_FOUND";
  private static final String EXPECTED_DESCRIPTION = "Could not find transaction id %s.";

  private CatchTransactionNotFoundExceptionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CatchTransactionNotFoundExceptionMapper();
  }

  @Test
  public void givenTransactionNotFoundException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    TransactionNotFoundException exception = new TransactionNotFoundException(AN_ID);

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription())
        .isEqualTo(String.format(EXPECTED_DESCRIPTION, AN_ID));
  }
}
