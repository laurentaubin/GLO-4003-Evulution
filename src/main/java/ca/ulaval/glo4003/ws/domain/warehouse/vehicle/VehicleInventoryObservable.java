package ca.ulaval.glo4003.ws.domain.warehouse.vehicle;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

import java.util.ArrayList;
import java.util.List;

public abstract class VehicleInventoryObservable {
  private final List<VehicleInventoryObserver> assembledObservers = new ArrayList<>();

  public void register(VehicleInventoryObserver observer) {
    assembledObservers.add(observer);
  }

  public void notifyVehicleInStock(Order order) {
    for (VehicleInventoryObserver observer : assembledObservers) {
      observer.listenToVehicleInStock(order);
    }
  }
}
