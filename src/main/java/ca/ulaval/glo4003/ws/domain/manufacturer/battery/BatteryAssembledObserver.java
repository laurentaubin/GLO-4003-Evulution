package ca.ulaval.glo4003.ws.domain.manufacturer.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;

public interface BatteryAssembledObserver {
  void listenToBatteryAssembled(BatteryOrder batteryOrder);
}
