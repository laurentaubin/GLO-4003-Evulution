package ca.ulaval.glo4003.ws.domain.warehouse.battery;

import ca.ulaval.glo4003.ws.domain.notification.BatteryOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import java.util.ArrayList;
import java.util.List;

public abstract class BatteryInventoryObservable {
  private final List<BatteryInventoryObserver> assembledObservers = new ArrayList<>();
  private final List<BatteryOrderDelayObserver> delayObservers = new ArrayList<>();

  public void register(BatteryInventoryObserver observer) {
    assembledObservers.add(observer);
  }

  public void register(BatteryOrderDelayObserver observer) {
    delayObservers.add(observer);
  }

  public void notifyBatteryInStock(Order order) {
    for (BatteryInventoryObserver observer : assembledObservers) {
      observer.listenToBatteryInStock(order);
    }
  }

  public void notifyBatteryDelay(Order order) {
    for (BatteryOrderDelayObserver observer : delayObservers) {
      observer.listenBatteryOrderDelay(order);
    }
  }
}
