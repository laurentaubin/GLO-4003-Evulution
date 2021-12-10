package ca.ulaval.glo4003.ws.domain.manufacturer.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import java.util.ArrayList;
import java.util.List;

public abstract class BatteryAssembledObservable {
  private final List<BatteryAssembledObserver> assembledObservers = new ArrayList<>();

  public void register(BatteryAssembledObserver observer) {
    assembledObservers.add(observer);
  }

  public void notifyBatteryAssembled(BatteryOrder batteryOrder) {
    for (BatteryAssembledObserver observer : assembledObservers) {
      observer.listenToBatteryAssembled(batteryOrder);
    }
  }
}
