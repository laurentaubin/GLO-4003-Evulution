package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

public interface VehicleAssembledObserver {
  void listenToVehicleAssembled(Order order);
}
