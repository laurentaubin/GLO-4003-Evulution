package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface VehicleAssembledObserver {
  void listenToVehicleAssembled(Order order);
}
