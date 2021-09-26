package ca.ulaval.glo4003.ws.domain.transaction;

public class Transaction {

  private TransactionId transactionId;
  private Vehicle vehicle;

  public Transaction() {
    this.transactionId = new TransactionId();
  }

  public Transaction(TransactionId transactionId) {
    this.transactionId = transactionId;
  }

  public TransactionId getId() {
    return transactionId;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }
}
