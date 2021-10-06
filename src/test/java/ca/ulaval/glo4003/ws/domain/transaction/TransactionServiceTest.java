package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
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
  private static final String A_BATTERY_TYPE = "STANDARD";
  private static final String A_VEHICLE_COLOR = Color.WHITE.toString();
  private static final String A_VEHICLE_MODEL = "a model";

  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionFactory transactionFactory;
  @Mock private BatteryRepository batteryRepository;
  @Mock private Vehicle aVehicle;
  @Mock private Payment payment;
  @Mock private Transaction aTransaction;

  private TransactionService transactionService;
  private Transaction transaction;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transaction.addVehicle(aVehicle);
    transactionService =
        new TransactionService(transactionRepository, transactionFactory, batteryRepository);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenRepositorySaveTransaction() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);

    // when
    transactionService.createTransaction();

    // then
    verify(transactionRepository).save(transaction);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenReturnTransaction() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);

    // when
    var actual = transactionService.createTransaction();

    // then
    assertThat(actual).isEqualTo(transaction);
  }

  @Test
  public void whenAddVehicle_thenAddVehicleToTransaction() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.of(aTransaction));

    // when
    transactionService.addVehicle(AN_ID, aVehicle);

    // then
    verify(aTransaction).addVehicle(aVehicle);
  }

  @Test
  void givenVehicleAndTransactionId_whenAddVehicle_thenRepositoryUpdatesTransaction() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.of(transaction));

    // when
    transactionService.addVehicle(AN_ID, aVehicle);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddVehicle_thenThrowTransactionNotFound() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addVehicle(AN_ID, aVehicle);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void givenBatteryAndTransactionId_whenAddBattery_thenRepositoryUpdateTransaction() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.of(transaction));

    // when
    transactionService.addBattery(AN_ID, A_BATTERY_TYPE);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddBattery_thenThrowTransactionNotFound() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addBattery(AN_ID, A_BATTERY_TYPE);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void givenPaymentAndTransactionId_whenAddPayment_thenRepositoryUpdateTransaction() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.of(aTransaction));

    // when
    transactionService.addPayment(AN_ID, payment);

    // then
    verify(transactionRepository).update(aTransaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddPayment_thenThrowTransactionNotFound() {
    // given
    given(transactionRepository.getTransaction(AN_ID)).willReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addPayment(AN_ID, payment);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }
}
