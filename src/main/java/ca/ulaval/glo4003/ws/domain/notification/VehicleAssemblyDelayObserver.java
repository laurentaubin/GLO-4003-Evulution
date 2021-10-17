package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

public interface VehicleAssemblyDelayObserver {
  void listenVehicleAssemblyDelay(Order order);
}
