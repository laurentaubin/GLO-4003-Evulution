package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.BatteryAssemblyDelayObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class BatteryAssemblyObservable {
  private final List<BatteryAssembledObserver> assembledObservers = new ArrayList<>();
  private final List<BatteryAssemblyDelayObserver> delayObservers = new ArrayList<>();

  public void register(BatteryAssembledObserver observer) {
    assembledObservers.add(observer);
  }

  public void register(BatteryAssemblyDelayObserver observer) {
    delayObservers.add(observer);
  }

  public void notifyBatteryCompleted(Order order) {
    for (BatteryAssembledObserver observer : assembledObservers) {
      observer.listenToBatteryAssembled(order);
    }
  }

  public void notifyBatteryAssemblyDelay(Order order) {
    for (BatteryAssemblyDelayObserver observer : delayObservers) {
      observer.listenBatteryAssemblyDelay(order);
    }
  }
}
