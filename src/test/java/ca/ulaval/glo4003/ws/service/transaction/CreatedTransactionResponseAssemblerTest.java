package ca.ulaval.glo4003.ws.service.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.service.transaction.dto.CreatedTransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatedTransactionResponseAssemblerTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");

  private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;

  @BeforeEach
  void setUp() {
    createdTransactionResponseAssembler = new CreatedTransactionResponseAssembler();
  }

  @Test
  void givenTransactionAndDelivery_whenAssemble_thenReturnCreatedTransactionResponseWithSameId() {
    // given
    Transaction transaction = new Transaction(AN_ID);
    Delivery delivery = new Delivery(A_DELIVERY_ID);

    // when
    CreatedTransactionResponse actual =
        createdTransactionResponseAssembler.assemble(transaction, delivery);

    // then
    assertThat(actual.transactionId).isEqualTo(AN_ID.toString());
    assertThat(actual.deliveryId).isEqualTo(A_DELIVERY_ID.toString());
  }
}
