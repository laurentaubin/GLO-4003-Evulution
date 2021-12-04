package ca.ulaval.glo4003.ws.api.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class BatteryResponse {

  @JsonProperty("estimated_range")
  public BigDecimal estimatedRange;
}
