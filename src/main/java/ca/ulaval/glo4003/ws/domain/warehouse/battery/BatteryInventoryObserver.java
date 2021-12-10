package ca.ulaval.glo4003.ws.domain.warehouse.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface BatteryInventoryObserver {
  void listenToBatteryInStock(Order order);
}
