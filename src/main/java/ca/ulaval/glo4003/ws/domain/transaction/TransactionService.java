package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import java.math.BigDecimal;

public class TransactionService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final TransactionRepository transactionRepository;
  private final TransactionFactory transactionFactory;
  private final BatteryRepository batteryRepository;
  private final TransactionCompletedObservable transactionCompletedObservable;

  public TransactionService() {
    this(
        serviceLocator.resolve(TransactionRepository.class),
        serviceLocator.resolve(TransactionFactory.class),
        serviceLocator.resolve(BatteryRepository.class),
        serviceLocator.resolve(TransactionCompletedObservable.class));
  }

  public TransactionService(
      TransactionRepository transactionRepository,
      TransactionFactory transactionFactory,
      BatteryRepository batteryRepository,
      TransactionCompletedObservable transactionCompletedObservable) {
    this.transactionRepository = transactionRepository;
    this.transactionFactory = transactionFactory;
    this.batteryRepository = batteryRepository;
    this.transactionCompletedObservable = transactionCompletedObservable;
  }

  public Transaction createTransaction() {
    Transaction transaction = transactionFactory.createTransaction();
    transactionRepository.save(transaction);
    return transaction;
  }

  public void addVehicle(TransactionId transactionId, Vehicle vehicle) {
    Transaction transaction = getTransaction(transactionId);
    transaction.addVehicle(vehicle);
    transactionRepository.update(transaction);
  }

  public void addBattery(TransactionId transactionId, String batteryRequestType) {
    Transaction transaction = getTransaction(transactionId);
    Battery battery = batteryRepository.findByType(batteryRequestType);
    transaction.addBattery(battery);
    transactionRepository.update(transaction);
  }

  public void addPayment(TransactionId transactionId, Payment payment) {
    Transaction transaction = getTransaction(transactionId);
    transaction.addPayment(payment);
    transactionRepository.update(transaction);
    transactionCompletedObservable.notifyTransactionCompleted(transaction);
  }

  public BigDecimal getVehicleEstimatedRange(TransactionId transactionId) {
    Transaction transaction = getTransaction(transactionId);
    return transaction.computeEstimatedVehicleRange();
  }

  private Transaction getTransaction(TransactionId transactionId) {
    return transactionRepository.find(transactionId);
  }
}
