package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.IncompleteTransactionException;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;

import java.math.BigDecimal;

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

  public void addBattery(Battery battery) {
    if (vehicle == null) {
      throw new CannotAddBatteryBeforeVehicleException();
    }
    vehicle.addBattery(battery);
  }

  public void addVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public BigDecimal computeEstimatedVehicleRange() {
    validate();
    return vehicle.computeRange();
  }

  public void addPayment(Payment payment) {
    validate();
    this.payment = payment;
  }

  public Payment getPayment() {
    return payment;
  }

  private void validate() {
    if (vehicle == null || !vehicle.hasBattery()) {
      throw new IncompleteTransactionException();
    }
  }
}
