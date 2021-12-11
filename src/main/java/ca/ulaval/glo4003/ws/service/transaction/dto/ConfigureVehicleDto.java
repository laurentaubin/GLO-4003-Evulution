package ca.ulaval.glo4003.ws.service.transaction.dto;

public class ConfigureVehicleDto {
  private final String modelName;
  private final String color;

  public ConfigureVehicleDto(String modelName, String color) {
    this.modelName = modelName;
    this.color = color;
  }

  public String getModelName() {
    return modelName;
  }

  public String getColor() {
    return color;
  }
}
