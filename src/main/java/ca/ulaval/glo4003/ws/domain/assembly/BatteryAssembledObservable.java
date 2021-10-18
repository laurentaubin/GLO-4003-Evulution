package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import java.util.ArrayList;
import java.util.List;

public abstract class BatteryAssembledObservable {
  private final List<BatteryAssembledObserver> observers = new ArrayList<>();

  public void register(BatteryAssembledObserver observer) {
    observers.add(observer);
  }

  public void notifyBatteryCompleted(Order order) {
    for (BatteryAssembledObserver observer : observers) {
      observer.listenToBatteryAssembled(order);
    }
  }
}
