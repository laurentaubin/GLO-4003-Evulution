package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.VehicleAssemblyDelayObserver;
import java.util.ArrayList;
import java.util.List;

public abstract class VehicleAssemblyObservable {
  private final List<VehicleAssembledObserver> assembledObservers = new ArrayList<>();
  private final List<VehicleAssemblyDelayObserver> observers = new ArrayList<>();

  public void register(VehicleAssembledObserver observer) {
    assembledObservers.add(observer);
  }

  public void register(VehicleAssemblyDelayObserver observer) {
    observers.add(observer);
  }

  public void notifyVehicleAssemblyDelay(Order order) {
    for (VehicleAssemblyDelayObserver observer : observers) {
      observer.listenVehicleAssemblyDelay(order);
    }
  }

  public void notifyVehicleAssembled(Order order) {
    for (VehicleAssembledObserver observer : assembledObservers) {
      observer.listenToVehicleAssembled(order);
    }
  }
}
