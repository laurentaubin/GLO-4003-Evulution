package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

public interface BatteryAssembledObserver {
  void listenToBatteryAssembled(Order order);
}
