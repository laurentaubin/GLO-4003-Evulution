package ca.ulaval.glo4003.ws.api.transaction.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigureVehicleRequest {

  @NotNull
  @JsonProperty(value = "name", required = true)
  private String model;

  private String color;

  public ConfigureVehicleRequest() {}

  public ConfigureVehicleRequest(String model, String color) {
    this.model = model;
    this.color = color;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }
}
