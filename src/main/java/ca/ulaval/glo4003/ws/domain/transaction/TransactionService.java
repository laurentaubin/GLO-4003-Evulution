package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;

public class TransactionService {

  private TransactionRepository transactionRepository;
  private TransactionFactory transactionFactory;
  private BatteryRepository batteryRepository;

  public TransactionService(
      TransactionRepository transactionRepository,
      TransactionFactory transactionFactory,
      BatteryRepository batteryRepository) {
    this.transactionRepository = transactionRepository;
    this.transactionFactory = transactionFactory;
    this.batteryRepository = batteryRepository;
  }

  public Transaction createTransaction() {
    Transaction transaction = transactionFactory.createTransaction();
    transactionRepository.save(transaction);
    return transaction;
  }

  public void addVehicle(TransactionId transactionId, Vehicle vehicle) {
    Transaction transaction = getTransaction(transactionId);
    transaction.setVehicle(vehicle);
    transactionRepository.update(transaction);
  }

  public Transaction addBattery(TransactionId transactionId, String batteryRequestType) {
    Transaction transaction = getTransaction(transactionId);
    Battery battery = batteryRepository.findByType(batteryRequestType);
    transaction.setBattery(battery);
    transactionRepository.update(transaction);
    return transaction;
  }

  private Transaction getTransaction(TransactionId transactionId) {
    return transactionRepository
        .getTransaction(transactionId)
        .orElseThrow(() -> new TransactionNotFoundException(transactionId));
  }

  public void addPayment(TransactionId transactionId, Payment payment) {
    Transaction transaction =
        transactionRepository
            .getTransaction(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(transactionId));

    transaction.setPayment(payment);
    transactionRepository.update(transaction);
  }
}
