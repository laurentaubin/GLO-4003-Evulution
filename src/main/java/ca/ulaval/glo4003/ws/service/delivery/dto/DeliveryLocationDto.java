package ca.ulaval.glo4003.ws.service.delivery.dto;

public class DeliveryLocationDto {

  private final String mode;
  private final String location;

  public DeliveryLocationDto(String mode, String location) {
    this.mode = mode;
    this.location = location;
  }

  public String getMode() {
    return mode;
  }

  public String getLocation() {
    return location;
  }
}
