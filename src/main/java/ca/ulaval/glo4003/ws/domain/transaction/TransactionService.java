package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;

public class TransactionService {

  private TransactionRepository transactionRepository;
  private TransactionHandler transactionHandler;
  private BatteryRepository batteryRepository;

  public TransactionService(
      TransactionRepository transactionRepository,
      TransactionHandler transactionHandler,
      BatteryRepository batteryRepository) {
    this.transactionRepository = transactionRepository;
    this.transactionHandler = transactionHandler;
    this.batteryRepository = batteryRepository;
  }

  public Transaction createTransaction() {
    Transaction transaction = transactionHandler.createTransaction();
    transactionRepository.save(transaction);
    return transaction;
  }

  public void addVehicle(TransactionId transactionId, Vehicle vehicle) {
    Transaction transaction = getTransaction(transactionId);
    transactionHandler.setVehicle(transaction, vehicle);
    transactionRepository.update(transaction);
  }

  public Transaction addBattery(TransactionId transactionId, String batteryRequestType) {
    Transaction transaction = getTransaction(transactionId);
    Battery battery = batteryRepository.findByType(batteryRequestType);
    transactionHandler.setBattery(transaction, battery);
    transactionRepository.update(transaction);
    return transaction;
  }

  private Transaction getTransaction(TransactionId transactionId) {
    return transactionRepository
        .getTransaction(transactionId)
        .orElseThrow(() -> new TransactionNotFoundException(transactionId));
  }
}
