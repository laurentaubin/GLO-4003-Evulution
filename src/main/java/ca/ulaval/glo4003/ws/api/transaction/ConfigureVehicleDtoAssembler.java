package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;

public class ConfigureVehicleDtoAssembler {
  public ConfigureVehicleDto assemble(ConfigureVehicleRequest vehicleConfigurationRequest) {
    return new ConfigureVehicleDto(
        vehicleConfigurationRequest.getModel(), vehicleConfigurationRequest.getColor());
  }
}
