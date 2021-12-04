package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BatteryResponseAssembler {

  public BatteryResponse assemble(BigDecimal estimatedRange) {
    BatteryResponse batteryResponse = new BatteryResponse();
    batteryResponse.estimatedRange = estimatedRange.setScale(2, RoundingMode.HALF_UP);
    return batteryResponse;
  }
}
