package ca.ulaval.glo4003.ws.infrastructure.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryInMemoryTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final Model A_MODEL = Model.VANDRY;
  private static final Color A_COLOR = new Color("color");

  private Transaction transaction;
  private TransactionRepository transactionRepository;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transactionRepository = new TransactionRepositoryInMemory();
  }

  @Test
  void givenSavedTransactionInRepository_whenGetTransaction_thenReturnTransaction() {
    // given
    transactionRepository.save(transaction);

    // when
    var actualTransaction = transactionRepository.getTransaction(transaction.getId()).get();

    // then
    assertThat(actualTransaction).isEqualTo(transaction);
  }

  @Test
  void givenUnexistingTransaction_whenGetTransaction_thenReturnEmpty() {
    // when
    var transaction = transactionRepository.getTransaction(AN_ID);

    // then
    assertThat(transaction.isEmpty()).isTrue();
  }

  @Test
  void givenTransactionAlreadyInRepository_whenSave_thenThrowDuplicateTransaction() {
    // given
    transactionRepository.save(transaction);

    // when
    Executable action = () -> transactionRepository.save(transaction);

    // given
    assertThrows(DuplicateTransactionException.class, action);
  }

  @Test
  void givenTransactionInRepository_whenUpdate_thenUpdateTransaction() {
    // given
    var updatedTransaction = createTransactionGivenId(AN_ID);
    updatedTransaction.setVehicle(createVehicle());
    transactionRepository.save(transaction);

    // when
    transactionRepository.update(updatedTransaction);

    // then
    var actualTransaction = transactionRepository.getTransaction(AN_ID).get();
    assertThat(actualTransaction).isEqualTo(updatedTransaction);
  }

  @Test
  void givenTransactionNotInRepository_whenUpdate_thenThrowTransactionNotFound() {
    // given
    var transaction = createTransactionGivenId(AN_ID);

    // when
    Executable action = () -> transactionRepository.update(transaction);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }

  private Vehicle createVehicle() {
    return new Vehicle(A_MODEL, A_COLOR);
  }
}
