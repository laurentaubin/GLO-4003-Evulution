package ca.ulaval.glo4003.ws.api.transaction.response;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionCreationResponseAssemblerTest {
  private static final String A_TRANSACTION_ID = "an id";
  private static final String A_DELIVERY_ID = "delivery id";

  private TransactionCreationResponseAssembler transactionCreationResponseAssembler;

  @BeforeEach
  public void setUp() {
    transactionCreationResponseAssembler = new TransactionCreationResponseAssembler();
  }

  @Test
  public void
      givenATransactionCreationDto_whenAssemble_thenReturnCorrectTransactionCreationResponse() {
    // given
    TransactionCreationDto transactionCreationDto =
        new TransactionCreationDto(A_TRANSACTION_ID, A_DELIVERY_ID);

    // when
    TransactionCreationResponse transactionCreationResponse =
        transactionCreationResponseAssembler.assemble(transactionCreationDto);

    // then
    assertThat(transactionCreationResponse.getTransactionId()).isEqualTo(A_TRANSACTION_ID);
    assertThat(transactionCreationResponse.getDeliveryId()).isEqualTo(A_DELIVERY_ID);
  }
}
