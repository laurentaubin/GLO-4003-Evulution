package ca.ulaval.glo4003.ws.api.transaction.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigureBatteryRequest {

  @NotNull
  @JsonProperty(required = true)
  private String type;

  public ConfigureBatteryRequest() {}

  public ConfigureBatteryRequest(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
