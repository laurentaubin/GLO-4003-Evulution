package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.IncompleteTransactionException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchIncompleteTransactionExceptionMapperTest {
  private static final int EXPECTED_STATUS_CODE = Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "TRANSACTION_INCOMPLETE";
  private static final String EXPECTED_DESCRIPTION =
      "Transaction is missing a vehicle and/or battery.";

  private CatchIncompleteTransactionExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchIncompleteTransactionExceptionMapper();
  }

  @Test
  public void givenIncompleteTransactionException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    IncompleteTransactionException incompleteTransactionException =
        new IncompleteTransactionException();

    // when
    Response response = mapper.toResponse(incompleteTransactionException);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
