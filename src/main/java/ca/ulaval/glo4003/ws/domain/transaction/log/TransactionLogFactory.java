package ca.ulaval.glo4003.ws.domain.transaction.log;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;

public class TransactionLogFactory {
  private final LocalDateProvider localDateProvider;

  public TransactionLogFactory(LocalDateProvider localDateProvider) {
    this.localDateProvider = localDateProvider;
  }

  public TransactionLogEntry create(Transaction transaction) {
    Vehicle transactionVehicle = transaction.getVehicle();

    return new TransactionLogEntry(
        localDateProvider.today(),
        transactionVehicle.getVehiclePrice(),
        transactionVehicle.getModel().getName(),
        transactionVehicle.getBattery().getType());
  }
}
