package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccountFactory;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.transaction.dto.*;
import java.math.BigDecimal;

public class TransactionService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final DeliveryService deliveryService;
  private final CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  private final BatteryResponseAssembler batteryResponseAssembler;
  private final VehicleFactory vehicleFactory;
  private final PaymentRequestAssembler paymentRequestAssembler;
  private final TransactionRepository transactionRepository;
  private final TransactionFactory transactionFactory;
  private final BatteryRepository batteryRepository;
  private final TransactionCompletedObservable transactionCompletedObservable;

  public TransactionService() {
    this(
        serviceLocator.resolve(DeliveryService.class),
        new CreatedTransactionResponseAssembler(),
        new BatteryResponseAssembler(),
        new VehicleFactory(),
        new PaymentRequestAssembler(new BankAccountFactory()),
        serviceLocator.resolve(TransactionRepository.class),
        serviceLocator.resolve(TransactionFactory.class),
        serviceLocator.resolve(BatteryRepository.class),
        serviceLocator.resolve(TransactionCompletedObservable.class));
  }

  public TransactionService(
      DeliveryService deliveryService,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      BatteryResponseAssembler batteryResponseAssembler,
      VehicleFactory vehicleFactory,
      PaymentRequestAssembler paymentRequestAssembler,
      TransactionRepository transactionRepository,
      TransactionFactory transactionFactory,
      BatteryRepository batteryRepository,
      TransactionCompletedObservable transactionCompletedObservable) {
    this.deliveryService = deliveryService;
    this.createdTransactionResponseAssembler = createdTransactionResponseAssembler;
    this.batteryResponseAssembler = batteryResponseAssembler;
    this.vehicleFactory = vehicleFactory;
    this.paymentRequestAssembler = paymentRequestAssembler;
    this.transactionRepository = transactionRepository;
    this.transactionFactory = transactionFactory;
    this.batteryRepository = batteryRepository;
    this.transactionCompletedObservable = transactionCompletedObservable;
  }

  public CreatedTransactionResponse createTransaction() {
    Transaction transaction = transactionFactory.createTransaction();
    transactionRepository.save(transaction);
    Delivery delivery = deliveryService.createDelivery();
    return createdTransactionResponseAssembler.assemble(transaction, delivery);
  }

  public void addVehicle(TransactionId transactionId, VehicleRequest vehicleRequest) {
    Vehicle vehicle = vehicleFactory.create(vehicleRequest.getModel(), vehicleRequest.getColor());
    Transaction transaction = getTransaction(transactionId);
    transaction.addVehicle(vehicle);
    transactionRepository.update(transaction);
  }

  public BatteryResponse addBattery(TransactionId transactionId, BatteryRequest batteryRequest) {
    Transaction transaction = getTransaction(transactionId);
    Battery battery = batteryRepository.findByType(batteryRequest.getType());
    transaction.addBattery(battery);
    transactionRepository.update(transaction);
    BigDecimal estimatedRange = transaction.computeEstimatedVehicleRange();
    return batteryResponseAssembler.assemble(estimatedRange);
  }

  public void completeTransaction(TransactionId transactionId, PaymentRequest paymentRequest) {
    Payment payment = paymentRequestAssembler.create(paymentRequest);
    Transaction transaction = getTransaction(transactionId);
    transaction.addPayment(payment);
    transactionRepository.update(transaction);
    transactionCompletedObservable.notifyTransactionCompleted(transaction);
  }

  private Transaction getTransaction(TransactionId transactionId) {
    return transactionRepository.find(transactionId);
  }
}
