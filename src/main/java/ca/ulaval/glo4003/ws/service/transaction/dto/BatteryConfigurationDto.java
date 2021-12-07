package ca.ulaval.glo4003.ws.service.transaction.dto;

import java.math.BigDecimal;

public class BatteryConfigurationDto {
  private final BigDecimal estimatedRange;

  public BatteryConfigurationDto(BigDecimal estimatedRange) {
    this.estimatedRange = estimatedRange;
  }

  public BigDecimal getEstimatedRange() {
    return estimatedRange;
  }
}
