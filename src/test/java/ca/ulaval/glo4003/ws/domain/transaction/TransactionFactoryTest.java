package ca.ulaval.glo4003.ws.domain.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class TransactionFactoryTest {
  private TransactionFactory transactionFactory;

  @BeforeEach
  void setUp() {
    transactionFactory = new TransactionFactory();
  }

  @Test
  void whenCreateTransaction_thenCreatedTransactionShouldHaveTransactionId() {
    // when
    Transaction transaction = transactionFactory.createTransaction();

    // then
    assertThat(transaction.getId()).isNotNull();
  }
}
