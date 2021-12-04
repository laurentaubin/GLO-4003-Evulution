package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.ModelAssemblyDelayObserver;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelAssemblyObservable {
  private final List<ModelAssembledObserver> assembledObservers = new ArrayList<>();
  private final List<ModelAssemblyDelayObserver> delayObservers = new ArrayList<>();

  public void register(ModelAssembledObserver observer) {
    assembledObservers.add(observer);
  }

  public void register(ModelAssemblyDelayObserver observer) {
    delayObservers.add(observer);
  }

  public void notifyModelAssembled(Order order) {
    for (ModelAssembledObserver observer : assembledObservers) {
      observer.listenToModelAssembled(order);
    }
  }

  public void notifyModelAssemblyDelay(Order order) {
    for (ModelAssemblyDelayObserver observer : delayObservers) {
      observer.listenModelAssemblyDelay(order);
    }
  }
}
