package ca.ulaval.glo4003.ws.api.transaction.response;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BatteryConfigurationResponseAssembler {

  public BatteryConfigurationResponse assemble(BigDecimal estimatedRange) {
    BatteryConfigurationResponse batteryResponse = new BatteryConfigurationResponse();
    batteryResponse.estimatedRange = estimatedRange.setScale(2, RoundingMode.HALF_UP);
    return batteryResponse;
  }
}
