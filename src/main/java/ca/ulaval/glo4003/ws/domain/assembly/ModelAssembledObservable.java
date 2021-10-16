package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelAssembledObservable {
  private final List<ModelAssembledObserver> observers = new ArrayList<>();

  public void register(ModelAssembledObserver observer) {
    observers.add(observer);
  }

  public void notifyModelAssembled(Order order) {
    for (ModelAssembledObserver observer : observers) {
      observer.listenToModelAssembled(order);
    }
  }
}
