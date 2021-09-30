package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.battery.Battery;

public class TransactionHandler {

  public Transaction createTransaction() {
    return new Transaction();
  }

  public Transaction setVehicle(Transaction transaction, Vehicle vehicle) {
    transaction.setVehicle(vehicle);
    return transaction;
  }

  public Transaction setBattery(Transaction transaction, Battery battery) {
    transaction.getVehicle().setBattery(battery);
    return transaction;
  }
}
