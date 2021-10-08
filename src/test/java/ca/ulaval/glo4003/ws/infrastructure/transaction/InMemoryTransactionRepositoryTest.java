package ca.ulaval.glo4003.ws.infrastructure.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryTransactionRepositoryTest {
  private static final TransactionId AN_ID = new TransactionId("id");

  private Transaction transaction;
  private TransactionRepository transactionRepository;

  @Mock private Vehicle aVehicle;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transactionRepository = new InMemoryTransactionRepository();
  }

  @Test
  void givenSavedTransactionInRepository_whenGetTransaction_thenReturnTransaction() {
    // given
    transactionRepository.save(transaction);

    // when
    Transaction actualTransaction = transactionRepository.find(transaction.getId());

    // then
    assertThat(actualTransaction).isEqualTo(transaction);
  }

  @Test
  void givenNonExistentTransaction_whenGetTransaction_thenThrowTransactionNotFoundException() {
    // when
    Executable findingTransaction = () -> transactionRepository.find(AN_ID);

    // then
    assertThrows(TransactionNotFoundException.class, findingTransaction);
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
    Transaction updatedTransaction = createTransactionGivenId(AN_ID);
    updatedTransaction.addVehicle(aVehicle);
    transactionRepository.save(transaction);

    // when
    transactionRepository.update(updatedTransaction);

    // then
    Transaction actualTransaction = transactionRepository.find(AN_ID);
    assertThat(actualTransaction).isEqualTo(updatedTransaction);
  }

  @Test
  void givenTransactionNotInRepository_whenUpdate_thenThrowTransactionNotFound() {
    // given
    Transaction transaction = createTransactionGivenId(AN_ID);

    // when
    Executable action = () -> transactionRepository.update(transaction);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }
}
