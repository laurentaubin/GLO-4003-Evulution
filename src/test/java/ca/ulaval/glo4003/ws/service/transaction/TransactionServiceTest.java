package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentFactory;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  private static final TransactionId AN_ID = TransactionId.fromString("id");
  private static final String A_BATTERY_TYPE = "type";
  private static final BigDecimal A_RANGE = BigDecimal.TEN;
  private static final String A_MODEL = "model";
  private static final String WHITE = "White";
  private static final List<Role> PRIVILEGED_ROLES = new ArrayList<>(List.of(Role.CUSTOMER));

  @Mock private DeliveryService deliveryService;
  @Mock private UserService userService;
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
  @Mock private TokenDto tokenDto;

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
            transactionCreationDtoAssembler,
                userService);
  }

  @Test
  public void whenCreateTransaction_thenTransactionCreated() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction(tokenDto);

    // then
    verify(transactionFactory).createTransaction();
  }

  @Test
  public void whenCreateTransaction_thenDeliveryCreated() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction(tokenDto);

    // then
    verify(deliveryService).createDelivery();
  }

  @Test
  void whenCreateTransaction_thenRepositorySaveTransaction() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction(tokenDto);

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
    transactionService.createTransaction(tokenDto);

    // then
    verify(transactionCreationDtoAssembler).assemble(transaction.getId(), delivery.getDeliveryId());
  }

  @Test
  public void
  whenCreateTransaction_thenVerifyIfUserIsAllowed() {
    // given
    given(transactionFactory.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionService.createTransaction(tokenDto);

    // then
    verify(userService).isAllowed(tokenDto, PRIVILEGED_ROLES);
  }

  @Test
  public void
      givenATransactionIdAndAConfigureVehicleDto_whenConfigureVehicle_thenVehicleIsCreatedFromDto() {
    // given
    given(configureVehicleDto.getModelName()).willReturn(A_MODEL);
    given(configureVehicleDto.getColor()).willReturn(A_WHITE_COLOR);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureVehicle(AN_ID, configureVehicleDto, tokenDto);

    // then
    verify(vehicleFactory).create(A_MODEL, A_WHITE_COLOR);
  }

  @Test
  public void
      givenTransactionIdAndAConfigureVehicleDto_whenConfigureVehicle_thenConfiguredVehicleIsAddedToTransaction() {
    // given
    given(vehicleFactory.create(any(), any())).willReturn(vehicle);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureVehicle(AN_ID, configureVehicleDto, tokenDto);

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
    transactionService.configureVehicle(AN_ID, configureVehicleDto, tokenDto);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenConfigureVehicle_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.configureVehicle(AN_ID, configureVehicleDto, tokenDto);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  void whenConfigureVehicle_thenValidateTransactionOwnership() {
    // given
    given(configureVehicleDto.getModelName()).willReturn(A_MODEL);
    given(configureVehicleDto.getColor()).willReturn(WHITE);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureVehicle(AN_ID, configureVehicleDto, tokenDto);

    // then
    verify(userService).validateTransactionOwnership(tokenDto, AN_ID, PRIVILEGED_ROLES);
  }

  @Test
  public void
      givenConfigureBatteryDtoAndATransactionId_whenConfigureBattery_thenDesiredBatteryIsFetch() {
    // given
    given(transactionRepository.find(any())).willReturn(transaction);
    given(configureBatteryDto.getTypeName()).willReturn(A_BATTERY_TYPE);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto, tokenDto);

    // then
    verify(batteryRepository).findByType(A_BATTERY_TYPE);
  }

  @Test
  public void givenConfigureBatteryDto_whenConfigureBattery_thenBatteryIsAddedToTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(batteryRepository.findByType(any())).willReturn(battery);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto, tokenDto);

    // then
    verify(transaction).addBattery(battery);
  }

  @Test
  public void whenConfigureBattery_thenValidateTransactionOwnership() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);
    given(batteryRepository.findByType(any())).willReturn(battery);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto, tokenDto);

    // then
    verify(userService).validateTransactionOwnership(tokenDto, AN_ID, PRIVILEGED_ROLES);
  }


  @Test
  void
      givenConfigureBatteryDtoAndTransactionId_whenConfigureBattery_thenRepositoryUpdateTransaction() {
    // given
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.configureBattery(AN_ID, configureBatteryDto, tokenDto);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenConfigureBattery_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.configureBattery(AN_ID, configureBatteryDto, tokenDto);

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
    transactionService.configureBattery(AN_ID, configureBatteryDto, tokenDto);

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
    transactionService.completeTransaction(AN_ID, configurePaymentDto, tokenDto);

    // then
    verify(transaction).addPayment(payment);
  }

  @Test
  public void
  whenCompleteTransaction_thenValidateTransactionOwnership() {
    // given
    given(paymentFactory.create(any(), any(), any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, configurePaymentDto, tokenDto);

    // then
    verify(userService).validateTransactionOwnership(tokenDto, AN_ID, PRIVILEGED_ROLES);
  }

  @Test
  void
      givenConfigurePaymentDtoAndTransactionId_whenCompleteTransaction_thenRepositoryUpdateTransaction() {
    // given
    given(paymentFactory.create(any(), any(), any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, configurePaymentDto, tokenDto);

    // then
    verify(transactionRepository).update(transaction);
  }

  @Test
  void givenNotExistingTransactionId_whenAddPayment_thenThrowTransactionNotFoundException() {
    // given
    given(transactionRepository.find(AN_ID)).willThrow(TransactionNotFoundException.class);

    // when
    Executable action = () -> transactionService.completeTransaction(AN_ID, configurePaymentDto, tokenDto);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void whenCompleteTransaction_thenNotifyTransactionCompletedObservers() {
    // given
    given(paymentFactory.create(any(), any(), any())).willReturn(payment);
    given(transactionRepository.find(AN_ID)).willReturn(transaction);

    // when
    transactionService.completeTransaction(AN_ID, configurePaymentDto, tokenDto);

    // then
    verify(transactionCompletedObservable).notifyTransactionCompleted(transaction);
  }
}
