package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;
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

  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionFactory transactionFactory;
  @Mock private BatteryRepository batteryRepository;
  @Mock private ModelRepository modelRepository;
  @Mock private VehicleRequest vehicleRequest;
  @Mock private Vehicle vehicle;
  @Mock private Payment payment;

  private TransactionService transactionService;
  private Transaction transaction;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transaction.addVehicle(vehicle);
    transactionService =
        new TransactionService(
            transactionRepository, transactionFactory, batteryRepository, modelRepository);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenRepositorySaveTransaction() {
    // given
    when(transactionFactory.createTransaction()).thenReturn(transaction);

    // when
    transactionService.createTransaction();

    // then
    verify(transactionRepository).save(transaction);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenReturnTransaction() {
    // given
    when(transactionFactory.createTransaction()).thenReturn(transaction);

    // when
    var actual = transactionService.createTransaction();

    // then
    assertThat(actual).isEqualTo(transaction);
  }

  @Test
  void givenVehicleAndTransactionId_whenAddVehicle_thenRepositoryUpdateTransaction() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.of(transaction));

    // when
    transactionService.addVehicle(AN_ID, vehicleRequest);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddVehicle_thenThrowTransactionNotFound() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addVehicle(AN_ID, vehicleRequest);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void givenBatteryAndTransactionId_whenAddBattery_thenRepositoryUpdateTransaction() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.of(transaction));

    // when
    transactionService.addBattery(AN_ID, A_BATTERY_TYPE);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddBattery_thenThrowTransactionNotFound() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addBattery(AN_ID, A_BATTERY_TYPE);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void givenPaymentAndTransactionId_whenAddPayment_thenRepositoryUpdateTransaction() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.of(transaction));

    // when
    transactionService.addPayment(AN_ID, payment);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddPayment_thenThrowTransactionNotFound() {
    // given
    when(transactionRepository.getTransaction(AN_ID)).thenReturn(Optional.empty());

    // when
    Executable action = () -> transactionService.addPayment(AN_ID, payment);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }
}
