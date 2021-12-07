package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionCompletedObservable;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionFactory;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccountFactory;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryConfigurationDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;
import java.math.BigDecimal;

public class TransactionService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final DeliveryService deliveryService;
  private final VehicleFactory vehicleFactory;
  private final TransactionRepository transactionRepository;
  private final TransactionFactory transactionFactory;
  private final BatteryRepository batteryRepository;
  private final TransactionCompletedObservable transactionCompletedObservable;
  private final BatteryConfigurationDtoAssembler batteryConfigurationDtoAssembler;
  private final PaymentFactory paymentFactory;
  private final TransactionCreationDtoAssembler transactionCreationDtoAssembler;

  public TransactionService() {
    this(
        serviceLocator.resolve(DeliveryService.class),
        new VehicleFactory(),
        serviceLocator.resolve(TransactionRepository.class),
        serviceLocator.resolve(TransactionFactory.class),
        serviceLocator.resolve(BatteryRepository.class),
        serviceLocator.resolve(TransactionCompletedObservable.class),
        new BatteryConfigurationDtoAssembler(),
        new PaymentFactory(new BankAccountFactory()),
        new TransactionCreationDtoAssembler());
  }

  public TransactionService(
      DeliveryService deliveryService,
      VehicleFactory vehicleFactory,
      TransactionRepository transactionRepository,
      TransactionFactory transactionFactory,
      BatteryRepository batteryRepository,
      TransactionCompletedObservable transactionCompletedObservable,
      BatteryConfigurationDtoAssembler batteryConfigurationDtoAssembler,
      PaymentFactory paymentFactory,
      TransactionCreationDtoAssembler transactionCreationDtoAssembler) {
    this.deliveryService = deliveryService;
    this.vehicleFactory = vehicleFactory;
    this.transactionRepository = transactionRepository;
    this.transactionFactory = transactionFactory;
    this.batteryRepository = batteryRepository;
    this.transactionCompletedObservable = transactionCompletedObservable;
    this.batteryConfigurationDtoAssembler = batteryConfigurationDtoAssembler;
    this.paymentFactory = paymentFactory;
    this.transactionCreationDtoAssembler = transactionCreationDtoAssembler;
  }

  public TransactionCreationDto createTransaction() {
    Transaction transaction = transactionFactory.createTransaction();
    transactionRepository.save(transaction);
    Delivery delivery = deliveryService.createDelivery();
    return transactionCreationDtoAssembler.assemble(transaction.getId(), delivery.getDeliveryId());
  }

  public void configureVehicle(
      TransactionId transactionId, ConfigureVehicleDto vehicleConfigurationDto) {
    Vehicle vehicle =
        vehicleFactory.create(
            vehicleConfigurationDto.getModelName(), vehicleConfigurationDto.getColor());
    Transaction transaction = getTransaction(transactionId);
    transaction.addVehicle(vehicle);
    transactionRepository.update(transaction);
  }

  public BatteryConfigurationDto configureBattery(
      TransactionId transactionId, ConfigureBatteryDto batteryConfigurationDto) {
    Transaction transaction = getTransaction(transactionId);
    Battery battery = batteryRepository.findByType(batteryConfigurationDto.getTypeName());
    transaction.addBattery(battery);
    transactionRepository.update(transaction);
    BigDecimal estimatedRange = transaction.computeEstimatedVehicleRange();
    return batteryConfigurationDtoAssembler.assemble(estimatedRange);
  }

  public void completeTransaction(
      TransactionId transactionId, ConfigurePaymentDto configurePaymentDto) {
    Payment payment =
        paymentFactory.create(
            configurePaymentDto.getBankNumber(),
            configurePaymentDto.getAccountNumber(),
            configurePaymentDto.getFrequency());
    Transaction transaction = getTransaction(transactionId);
    transaction.addPayment(payment);
    transactionRepository.update(transaction);
    transactionCompletedObservable.notifyTransactionCompleted(transaction);
  }

  private Transaction getTransaction(TransactionId transactionId) {
    return transactionRepository.find(transactionId);
  }
}
