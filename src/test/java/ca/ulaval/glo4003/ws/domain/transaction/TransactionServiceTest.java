package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final Model A_MODEL = Model.VANDRY;
  private static final Color A_COLOR = new Color("color");

  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionHandler transactionHandler;

  private TransactionService transactionService;
  private Transaction transaction;
  private Vehicle vehicle;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    vehicle = createVehicle();
    transactionService = new TransactionService(transactionRepository, transactionHandler);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenRepositorySaveTransaction() {
    // given
    when(transactionHandler.createTransaction()).thenReturn(transaction);

    // when
    transactionService.createTransaction();

    // then
    verify(transactionRepository).save(transaction);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenReturnTransaction() {
    // given
    when(transactionHandler.createTransaction()).thenReturn(transaction);

    // when
    var actual = transactionService.createTransaction();

    // then
    assertThat(actual).isEqualTo(transaction);
  }

  @Test
  void givenVehicleAndTransactionId_whenAddVehicle_thenFactorySetVehicle() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.of(transaction));

    // when
    transactionService.addVehicle(AN_ID, vehicle);

    // then
    verify(transactionHandler).setVehicle(transaction, vehicle);
  }

  @Test
  void givenVehicleAndTransactionId_whenAddVehicle_thenRepositoryUpdateTransaction() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.of(transaction));
    when(transactionHandler.setVehicle(transaction, vehicle)).thenReturn(transaction);

    // when
    transactionService.addVehicle(AN_ID, vehicle);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddVehicle_thenThrowTransactionNotFound() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addVehicle(AN_ID, vehicle);

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
