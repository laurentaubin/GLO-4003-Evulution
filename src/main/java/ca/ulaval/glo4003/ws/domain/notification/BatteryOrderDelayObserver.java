package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface BatteryOrderDelayObserver {
  void listenBatteryOrderDelay(Order order);
}
