package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final String A_BATTERY_TYPE = "STANDARD";

  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionFactory transactionFactory;
  @Mock private BatteryRepository batteryRepository;
  @Mock private Vehicle aVehicle;
  @Mock private Payment payment;
  @Mock private Transaction aTransaction;
  @Mock private TransactionCompletedObservable transactionCompletedObservable;

  private TransactionService transactionService;
  private Transaction transaction;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transaction.addVehicle(aVehicle);
    transactionService =
        new TransactionService(
            transactionRepository,
            transactionFactory,
            batteryRepository,
            transactionCompletedObservable);
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
    given(transactionRepository.find(AN_ID)).willReturn(aTransaction);

    // when
    transactionService.addVehicle(AN_ID, aVehicle);

    // then
    verify(aTransaction).addVehicle(aVehicle);
  }

  @Test
  void givenVehicleAndTransactionId_whenAddVehicle_thenRepositoryUpdatesTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.addVehicle(AN_ID, aVehicle);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddVehicle_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.addVehicle(AN_ID, aVehicle);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void givenBatteryAndTransactionId_whenAddBattery_thenRepositoryUpdateTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.addBattery(AN_ID, A_BATTERY_TYPE);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddBattery_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.addBattery(AN_ID, A_BATTERY_TYPE);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void givenPaymentAndTransactionId_whenAddPayment_thenRepositoryUpdateTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(aTransaction);

    // when
    transactionService.addPayment(AN_ID, payment);

    // then
    verify(transactionRepository).update(aTransaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddPayment_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.addPayment(AN_ID, payment);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void whenAddPayment_thenNotifyTransactionCompletedObservers() {
    // given
    givenATransactionReadyToBeCompleted();

    // when
    transactionService.addPayment(AN_ID, payment);

    // then
    verify(transactionCompletedObservable).notifyTransactionCompleted(transaction);
  }

  private void givenATransactionReadyToBeCompleted() {
    given(aVehicle.hasBattery()).willReturn(true);
    transaction.addVehicle(aVehicle);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }
}
