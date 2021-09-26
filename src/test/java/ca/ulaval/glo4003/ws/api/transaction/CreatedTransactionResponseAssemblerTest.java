package ca.ulaval.glo4003.ws.api.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatedTransactionResponseAssemblerTest {
  private static final TransactionId AN_ID = new TransactionId("id");

  private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;

  @BeforeEach
  void setUp() {
    createdTransactionResponseAssembler = new CreatedTransactionResponseAssembler();
  }

  @Test
  void givenTransaction_whenCreate_shouldReturnCreatedTransactionResponseWithSameId() {
    // given
    var transaction = new Transaction(AN_ID);

    // when
    var actual = createdTransactionResponseAssembler.create(transaction);

    // then
    assertThat(actual.transactionId).matches(transaction.getId().toString());
  }
}
