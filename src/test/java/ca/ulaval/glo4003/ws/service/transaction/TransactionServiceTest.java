package ca.ulaval.glo4003.ws.service.transaction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionCompletedObservable;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionFactory;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.VehicleRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  private static final TransactionId AN_ID = TransactionId.fromString("id");
  private static final String BATTERY_TYPE = "type";
  private static final BigDecimal A_RANGE = BigDecimal.TEN;
  private static final String A_MODEL = "model";
  private static final String WHITE = "White";

  @Mock private DeliveryService deliveryService;
  @Mock private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  @Mock private BatteryResponseAssembler batteryResponseAssembler;
  @Mock private VehicleFactory vehicleFactory;
  @Mock private PaymentRequestAssembler paymentRequestAssembler;
  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionFactory transactionFactory;
  @Mock private BatteryRepository batteryRepository;
  @Mock private TransactionCompletedObservable transactionCompletedObservable;

  @Mock private Transaction transaction;
  @Mock private Delivery delivery;
  @Mock private Vehicle vehicle;
  @Mock private Battery battery;
  @Mock private Payment payment;

  private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    transactionService =
        new TransactionService(
            deliveryService,
            createdTransactionResponseAssembler,
            batteryResponseAssembler,
            vehicleFactory,
            paymentRequestAssembler,
            transactionRepository,
            transactionFactory,
            batteryRepository,
            transactionCompletedObservable);
  }

  @Test
  public void whenCreateTransaction_thenTransactionCreated() {
    // when
    transactionService.createTransaction();

    // then
    verify(transactionFactory).createTransaction();
  }

  @Test
  public void whenCreateTransaction_thenDeliveryCreated() {
    // when
    transactionService.createTransaction();

    // then
    verify(deliveryService).createDelivery();
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
  public void
      givenCreatedTransactionAndDelivery_whenCreatedTransaction_createdTransactionResponseAssembled() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction();

    // then
    verify(createdTransactionResponseAssembler).assemble(transaction, delivery);
  }

  @Test
  public void givenTransactionIdVehicleRequest_whenAddVehicle_thenVehicleCreatedByFactory() {
    // given
    var vehicleRequest = createVehicleRequest();
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.addVehicle(AN_ID, vehicleRequest);

    // then
    verify(vehicleFactory).create(A_MODEL, WHITE);
  }

  @Test
  public void givenTransactionIdAndVehicleRequest_whenAddVehicle_thenVehicleAddedToTransaction() {
    // given
    var vehicleRequest = createVehicleRequest();

    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(vehicleFactory.create(A_MODEL, WHITE)).willReturn(vehicle);

    // when
    transactionService.addVehicle(AN_ID, vehicleRequest);

    // then
    verify(transaction).addVehicle(vehicle);
  }

  @Test
  public void givenTransactionIdAndVehicleRequest_whenAddVehicle_thenTransactionUpdated() {
    // given
    var vehicleRequest = createVehicleRequest();

    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(vehicleFactory.create(A_MODEL, WHITE)).willReturn(vehicle);

    // when
    transactionService.addVehicle(AN_ID, vehicleRequest);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddVehicle_thenThrowTransactionNotFoundException() {
    // given
    var vehicleRequest = createVehicleRequest();
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.addVehicle(AN_ID, vehicleRequest);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void givenBatteryRequest_whenAddBattery_thenBatteryAddedToTransaction() {
    // given
    var batteryRequest = new BatteryRequest();
    batteryRequest.setType(BATTERY_TYPE);

    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(batteryRepository.findByType(any())).willReturn(battery);

    // when
    transactionService.addBattery(AN_ID, batteryRequest);

    // then
    verify(transaction).addBattery(battery);
  }

  @Test
  void givenBatteryAndTransactionId_whenAddBattery_thenRepositoryUpdateTransaction() {
    // given
    var batteryRequest = new BatteryRequest();
    batteryRequest.setType(BATTERY_TYPE);

    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.addBattery(AN_ID, batteryRequest);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddBattery_thenThrowTransactionNotFoundException() {
    // given
    var batteryRequest = new BatteryRequest();
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.addBattery(AN_ID, batteryRequest);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void givenTransactionId_whenAddBattery_thenBatteryResponseAssembledWithEstimatedRange() {
    // given
    var batteryRequest = new BatteryRequest();
    batteryRequest.setType(BATTERY_TYPE);

    given(transaction.computeEstimatedVehicleRange()).willReturn(A_RANGE);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.addBattery(AN_ID, batteryRequest);

    // then
    verify(batteryResponseAssembler).assemble(A_RANGE);
  }

  @Test
  public void
      givenTransactionIdAndPaymentRequest_whenCompleteTransaction_thenPaymentAddedToTransaction() {
    // given
    given(paymentRequestAssembler.create(any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, new PaymentRequest());

    // then
    verify(transaction).addPayment(payment);
  }

  @Test
  void givenPaymentAndTransactionId_whenAddPayment_thenRepositoryUpdateTransaction() {
    // given
    given(paymentRequestAssembler.create(any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, new PaymentRequest());

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddPayment_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.completeTransaction(AN_ID, new PaymentRequest());

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void whenAddPayment_thenNotifyTransactionCompletedObservers() {
    // given
    given(paymentRequestAssembler.create(any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, new PaymentRequest());

    // then
    verify(transactionCompletedObservable).notifyTransactionCompleted(transaction);
  }

  private VehicleRequest createVehicleRequest() {
    var request = new VehicleRequest();
    request.setModel(A_MODEL);
    request.setColor(WHITE);
    return request;
  }
}
