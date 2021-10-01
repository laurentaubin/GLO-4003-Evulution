package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.battery.Battery;

public class Transaction {

  private final TransactionId transactionId;
  private Vehicle vehicle;
  private Payment payment;

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

  public Integer computeRange() {
    return vehicle.computeRange();
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  public void setBattery(Battery battery) {
    vehicle.setBattery(battery);
  }
}
