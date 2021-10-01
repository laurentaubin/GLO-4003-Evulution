package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
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

  public BigDecimal computeEstimatedVehicleRange() {
    return vehicle.computeRange();
  }

  public void addPayment(Payment payment) {
    this.payment = payment;
  }
}
