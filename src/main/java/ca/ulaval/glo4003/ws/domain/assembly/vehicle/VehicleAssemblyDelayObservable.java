package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.VehicleAssemblyDelayObserver;
import java.util.ArrayList;
import java.util.List;

public abstract class VehicleAssemblyDelayObservable {
  private final List<VehicleAssemblyDelayObserver> observers = new ArrayList<>();

  public void register(VehicleAssemblyDelayObserver observer) {
    observers.add(observer);
  }

  public void notifyVehicleAssemblyDelay(Order order) {
    for (VehicleAssemblyDelayObserver observer : observers) {
      observer.listenVehicleAssemblyDelay(order);
    }
  }
}
