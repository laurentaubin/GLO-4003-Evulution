package ca.ulaval.glo4003.ws.domain.transaction;

public class TransactionHandler {

  public Transaction createTransaction() {
    return new Transaction();
  }

  public Transaction setVehicle(Transaction transaction, Vehicle vehicle) {
    transaction.setVehicle(vehicle);
    return transaction;
  }
}
