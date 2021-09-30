package ca.ulaval.glo4003.ws.infrastructure.battery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatteryDto {

  @JsonProperty(value = "name", required = true)
  public String type;

  @JsonProperty public String base_NRCAN_range;
  @JsonProperty public int capacity;
  @JsonProperty public int price;
  @JsonProperty public String time_to_produce;
}
