package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InMemoryTransactionRepositoryTest {
  private static final TransactionId AN_ID = new TransactionId("id");

  private final TransactionAssembler transactionAssembler = new TransactionAssembler();
  private Transaction transaction;

  @Mock private Vehicle aVehicle;

  private InMemoryTransactionRepository transactionRepository;

  @BeforeEach
  public void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transactionRepository = new InMemoryTransactionRepository(transactionAssembler);
  }

  @Test
  public void givenSavedTransactionInRepository_whenGetTransaction_thenReturnTransaction() {
    // given
    transactionRepository.save(transaction);

    // when
    Transaction actualTransaction = transactionRepository.find(transaction.getId());

    // then
    assertThat(actualTransaction.getId()).isEqualTo(transaction.getId());
  }

  @Test
  public void
      givenNonExistentTransaction_whenGetTransaction_thenThrowTransactionNotFoundException() {
    // when
    Executable findingTransaction = () -> transactionRepository.find(AN_ID);

    // then
    assertThrows(TransactionNotFoundException.class, findingTransaction);
  }

  @Test
  public void givenTransactionAlreadyInRepository_whenSave_thenThrowDuplicateTransaction() {
    // given
    transactionRepository.save(transaction);

    // when
    Executable action = () -> transactionRepository.save(transaction);

    // given
    assertThrows(DuplicateTransactionException.class, action);
  }

  @Test
  public void givenTransactionInRepository_whenUpdate_thenUpdateTransaction() {
    // given
    Transaction updatedTransaction = createTransactionGivenId(AN_ID);
    updatedTransaction.addVehicle(aVehicle);
    transactionRepository.save(transaction);

    // when
    transactionRepository.update(updatedTransaction);

    // then
    Transaction actualTransaction = transactionRepository.find(AN_ID);
    assertThat(actualTransaction.getVehicle()).isEqualTo(aVehicle);
  }

  @Test
  public void givenTransactionNotInRepository_whenUpdate_thenThrowTransactionNotFound() {
    // given
    Transaction transaction = createTransactionGivenId(AN_ID);

    // when
    Executable action = () -> transactionRepository.update(transaction);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void
      givenTransactionSaved_whenAddVehicleToOriginalTransaction_thenSavedTransactionIsNotAffected() {
    // given
    Transaction transaction = createTransactionGivenId(AN_ID);
    transactionRepository.save(transaction);

    // when
    transaction.addVehicle(aVehicle);
    Transaction originalTransaction = transactionRepository.find(AN_ID);

    // then
    assertThat(originalTransaction.getVehicle()).isNotEqualTo(aVehicle);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }
}
