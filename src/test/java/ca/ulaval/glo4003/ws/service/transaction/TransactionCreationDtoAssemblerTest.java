package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class TransactionCreationDtoAssemblerTest {
  private static final String ANOTHER_ID = "another id";
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId(ANOTHER_ID);

  private TransactionCreationDtoAssembler transactionCreationDtoAssembler;

  @BeforeEach
  public void setUp() {
    transactionCreationDtoAssembler = new TransactionCreationDtoAssembler();
  }

  @Test
  public void
      givenATransactionIdAndDeliveryId_whenAssemble_thenTransactionCreationDtoIsCorrectlyAssemble() {
    // when
    TransactionCreationDto transactionCreationDto =
        transactionCreationDtoAssembler.assemble(A_TRANSACTION_ID, A_DELIVERY_ID);

    // then
    assertThat(transactionCreationDto.getTransactionId()).isEqualTo(A_TRANSACTION_ID.getId());
    assertThat(transactionCreationDto.getDeliveryId()).isEqualTo(A_DELIVERY_ID.getDeliveryId());
  }
}
