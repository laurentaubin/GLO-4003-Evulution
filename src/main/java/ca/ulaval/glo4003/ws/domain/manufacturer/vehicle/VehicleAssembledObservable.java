package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.domain.notification.VehicleOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

import java.util.ArrayList;
import java.util.List;

public class VehicleAssembledObservable {
  private final List<VehicleAssembledObserver> assembledObservers = new ArrayList<>();
  private final List<VehicleOrderDelayObserver> delayObservers = new ArrayList<>();

  public void register(VehicleAssembledObserver observer) {
    assembledObservers.add(observer);
  }

  public void register(VehicleOrderDelayObserver observer) {
    delayObservers.add(observer);
  }

  public void notifyVehicleAssembled(Order order) {
    for (VehicleAssembledObserver observer : assembledObservers) {
      observer.listenToVehicleAssembled(order);
    }
  }

  public void notifyVehicleOrderDelay(Order order) {
    for (VehicleOrderDelayObserver observer : delayObservers) {
      observer.listenVehicleOrderDelay(order);
    }
  }
}
