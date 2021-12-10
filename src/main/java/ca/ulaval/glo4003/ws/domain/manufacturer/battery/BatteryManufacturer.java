package ca.ulaval.glo4003.ws.domain.manufacturer.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public interface BatteryManufacturer {
  void addOrder(BatteryOrder batteryOrder);

  AssemblyTime computeRemainingTimeToProduceNextBatteryType(String batteryType);
}
