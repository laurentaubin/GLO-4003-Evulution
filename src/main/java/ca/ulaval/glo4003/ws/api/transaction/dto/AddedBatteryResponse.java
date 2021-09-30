package ca.ulaval.glo4003.ws.api.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddedBatteryResponse {

  @JsonProperty("estimated_range")
  public Integer estimatedRange;

  public AddedBatteryResponse(Integer estimatedRange) {
    this.estimatedRange = estimatedRange;
  }
}
