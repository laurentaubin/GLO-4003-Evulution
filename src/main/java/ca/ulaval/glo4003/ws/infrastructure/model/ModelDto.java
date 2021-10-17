package ca.ulaval.glo4003.ws.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ModelDto {
  @JsonProperty public String name;
  @JsonProperty public String style;

  @JsonProperty(value = "efficiency_equivalence_rate")
  public BigDecimal efficiencyEquivalenceRate;

  @JsonProperty(value = "base_price")
  public Integer basePrice;

  @JsonProperty(value = "time_to_produce")
  public String timeToProduce;
}
