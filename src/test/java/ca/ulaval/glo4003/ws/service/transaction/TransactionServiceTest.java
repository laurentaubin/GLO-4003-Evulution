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
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
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
  @Mock private VehicleFactory vehicleFactory;
  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionFactory transactionFactory;
  @Mock private BatteryRepository batteryRepository;
  @Mock private TransactionCompletedObservable transactionCompletedObservable;
  @Mock private BatteryConfigurationDtoAssembler batteryConfigurationDtoAssembler;
  @Mock private PaymentFactory paymentFactory;
  @Mock private TransactionCreationDtoAssembler transactionCreationDtoAssembler;

  @Mock private Transaction transaction;
  @Mock private Delivery delivery;
  @Mock private Vehicle vehicle;
  @Mock private Battery battery;
  @Mock private Payment payment;
  @Mock private ConfigureVehicleDto configureVehicleDto;
  @Mock private ConfigureBatteryDto configureBatteryDto;
  @Mock private ConfigurePaymentDto configurePaymentDto;

  private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    transactionService =
        new TransactionService(
            deliveryService,
            vehicleFactory,
            transactionRepository,
            transactionFactory,
            batteryRepository,
            transactionCompletedObservable,
            batteryConfigurationDtoAssembler,
            paymentFactory,
            transactionCreationDtoAssembler);
  }

  @Test
  public void whenCreateTransaction_thenTransactionCreated() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction();

    // then
    verify(transactionFactory).createTransaction();
  }

  @Test
  public void whenCreateTransaction_thenDeliveryCreated() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction();

    // then
    verify(deliveryService).createDelivery();
  }

  @Test
  void whenCreateTransaction_thenRepositorySaveTransaction() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction();

    // then
    verify(transactionRepository).save(transaction);
  }

  @Test
  public void
      whenCreateTransaction_thenTransactionCreationDtoIsAssembledFromCreatedTransactionAndDelivery() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction();

    // then
    verify(transactionCreationDtoAssembler).assemble(transaction.getId(), delivery.getDeliveryId());
  }

  @Test
  public void
      givenATransactionIdAndAConfigureVehicleDto_whenConfigureVehicle_thenVehicleIsCreatedFromDto() {
    // given
    given(configureVehicleDto.getModelName()).willReturn(A_MODEL);
    given(configureVehicleDto.getColor()).willReturn(WHITE);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureVehicle(AN_ID, configureVehicleDto);

    // then
    verify(vehicleFactory).create(A_MODEL, WHITE);
  }

  @Test
  public void
      givenTransactionIdAndAConfigureVehicleDto_whenConfigureVehicle_thenConfiguredVehicleIsAddedToTransaction() {
    // given
    given(vehicleFactory.create(any(), any())).willReturn(vehicle);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureVehicle(AN_ID, configureVehicleDto);

    // then
    verify(transaction).addVehicle(vehicle);
  }

  @Test
  public void
      givenTransactionIdAndAConfigureVehicleDto_whenConfigureVehicle_thenTransactionUpdated() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(vehicleFactory.create(any(), any())).willReturn(vehicle);

    // when
    transactionService.configureVehicle(AN_ID, configureVehicleDto);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenConfigureVehicle_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.configureVehicle(AN_ID, configureVehicleDto);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void
      givenConfigureBatteryDtoAndATransactionId_whenConfigureBattery_thenDesiredBatteryIsFetch() {
    // given
    given(transactionRepository.find(any())).willReturn(transaction);
    given(configureBatteryDto.getTypeName()).willReturn(BATTERY_TYPE);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto);

    // then
    verify(batteryRepository).findByType(BATTERY_TYPE);
  }

  @Test
  public void givenConfigureBatteryDto_whenConfigureBattery_thenBatteryIsAddedToTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(batteryRepository.findByType(any())).willReturn(battery);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto);

    // then
    verify(transaction).addBattery(battery);
  }

  @Test
  void
      givenConfigureBatteryDtoAndTransactionId_whenConfigureBattery_thenRepositoryUpdateTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddBattery_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.configureBattery(AN_ID, configureBatteryDto);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void
      givenTransactionId_whenConfigureBattery_thenBatteryConfigurationDtoAssembledWithEstimatedRange() {
    // given
    given(transaction.computeEstimatedVehicleRange()).willReturn(A_RANGE);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto);

    // then
    verify(batteryConfigurationDtoAssembler).assemble(A_RANGE);
  }

  @Test
  public void
      givenTransactionIdAndPaymentConfigurationDto_whenCompleteTransaction_thenPaymentAddedToTransaction() {
    // given
    given(paymentFactory.create(any(), any(), any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, configurePaymentDto);

    // then
    verify(transaction).addPayment(payment);
  }

  @Test
  void
      givenConfigurePaymentDtoAndTransactionId_whenCompleteTransaction_thenRepositoryUpdateTransaction() {
    // given
    given(paymentFactory.create(any(), any(), any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, configurePaymentDto);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddPayment_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.completeTransaction(AN_ID, configurePaymentDto);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void whenCompleteTransaction_thenNotifyTransactionCompletedObservers() {
    // given
    given(paymentFactory.create(any(), any(), any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, configurePaymentDto);

    // then
    verify(transactionCompletedObservable).notifyTransactionCompleted(transaction);
  }
}
