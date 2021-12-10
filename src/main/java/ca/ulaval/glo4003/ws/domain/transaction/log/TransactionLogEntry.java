package ca.ulaval.glo4003.ws.domain.transaction.log;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;

import java.time.LocalDate;
import java.util.Objects;

public class TransactionLogEntry {
  private final LocalDate creationDate;
  private final Price totalPrice;
  private final String vehicleModel;
  private final String batteryType;

  public TransactionLogEntry(
      LocalDate creationDate, Price totalPrice, String vehicleModel, String batteryType) {
    this.creationDate = creationDate;
    this.totalPrice = totalPrice;
    this.vehicleModel = vehicleModel;
    this.batteryType = batteryType;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public Price getTotalPrice() {
    return totalPrice;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public String getBatteryType() {
    return batteryType;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof TransactionLogEntry)) {
      return false;
    }
    TransactionLogEntry transactionLogEntry = (TransactionLogEntry) o;
    return creationDate.equals(transactionLogEntry.creationDate)
        && totalPrice.equals(transactionLogEntry.totalPrice)
        && vehicleModel.equals(transactionLogEntry.vehicleModel)
        && batteryType.equals(transactionLogEntry.batteryType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(creationDate, totalPrice, vehicleModel, batteryType);
  }
}
