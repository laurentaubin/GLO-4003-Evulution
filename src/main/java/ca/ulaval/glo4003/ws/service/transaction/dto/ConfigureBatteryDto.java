package ca.ulaval.glo4003.ws.service.transaction.dto;

public class ConfigureBatteryDto {
  private final String typeName;

  public ConfigureBatteryDto(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }
}
