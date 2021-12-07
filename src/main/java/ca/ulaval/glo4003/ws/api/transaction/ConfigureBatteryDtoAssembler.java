package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;

public class ConfigureBatteryDtoAssembler {
  public ConfigureBatteryDto assemble(ConfigureBatteryRequest batteryConfigurationRequest) {
    return new ConfigureBatteryDto(batteryConfigurationRequest.getType());
  }
}
