package ca.ulaval.glo4003.ws.api.delivery.request;

import jakarta.validation.constraints.NotBlank;

public class DeliveryLocationRequest {
  @NotBlank private String mode;

  @NotBlank private String location;

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
