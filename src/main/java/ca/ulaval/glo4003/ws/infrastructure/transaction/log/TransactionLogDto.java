package ca.ulaval.glo4003.ws.infrastructure.transaction.log;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionLogDto {
  private final LocalDate creationDate;
  private final BigDecimal totalPrice;
  private final String modelName;
  private final String batteryType;

  public TransactionLogDto(
      LocalDate creationDate, BigDecimal totalPrice, String modelName, String batteryType) {
    this.creationDate = creationDate;
    this.totalPrice = totalPrice;
    this.modelName = modelName;
    this.batteryType = batteryType;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public String getModelName() {
    return modelName;
  }

  public String getBatteryType() {
    return batteryType;
  }
}
