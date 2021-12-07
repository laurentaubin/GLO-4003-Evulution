package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryConfigurationDto;
import java.math.BigDecimal;

public class BatteryConfigurationDtoAssembler {
  public BatteryConfigurationDto assemble(BigDecimal estimatedRange) {
    return new BatteryConfigurationDto(estimatedRange);
  }
}
