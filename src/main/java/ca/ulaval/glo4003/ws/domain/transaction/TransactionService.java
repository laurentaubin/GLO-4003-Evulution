package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;

public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionFactory transactionFactory;
  private final BatteryRepository batteryRepository;
  private final ModelRepository modelRepository;

  public TransactionService(
      TransactionRepository transactionRepository,
      TransactionFactory transactionFactory,
      BatteryRepository batteryRepository,
      ModelRepository modelRepository) {
    this.transactionRepository = transactionRepository;
    this.transactionFactory = transactionFactory;
    this.batteryRepository = batteryRepository;
    this.modelRepository = modelRepository;
  }

  public Transaction createTransaction() {
    Transaction transaction = transactionFactory.createTransaction();
    transactionRepository.save(transaction);
    return transaction;
  }

  public void addVehicle(TransactionId transactionId, VehicleRequest vehicleRequest) {
    Transaction transaction = getTransaction(transactionId);
    Model model = modelRepository.findByModel(vehicleRequest.getModel());
    Vehicle vehicle = new Vehicle(model, new Color(vehicleRequest.getColor()));
    transaction.addVehicle(vehicle);
    transactionRepository.update(transaction);
  }

  public Transaction addBattery(TransactionId transactionId, String batteryRequestType) {
    Transaction transaction = getTransaction(transactionId);
    Battery battery = batteryRepository.findByType(batteryRequestType);
    transaction.addBattery(battery);
    transactionRepository.update(transaction);
    return transaction;
  }

  public void addPayment(TransactionId transactionId, Payment payment) {
    Transaction transaction = getTransaction(transactionId);
    transaction.addPayment(payment);
    transactionRepository.update(transaction);
  }

  private Transaction getTransaction(TransactionId transactionId) {
    return transactionRepository
        .getTransaction(transactionId)
        .orElseThrow(() -> new TransactionNotFoundException(transactionId));
  }
}
