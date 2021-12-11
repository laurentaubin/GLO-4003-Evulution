package ca.ulaval.glo4003.ws.api.transaction.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BatteryConfigurationResponse {

  @JsonProperty("estimated_range")
  public BigDecimal estimatedRange;
}
