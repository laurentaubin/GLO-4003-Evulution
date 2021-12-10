package ca.ulaval.glo4003.ws.domain.warehouse.vehicle;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface VehicleInventoryObserver {
  void listenToVehicleInStock(Order order);
}
