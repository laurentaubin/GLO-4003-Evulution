package ca.ulaval.glo4003.ws.domain.warehouse.model;

import ca.ulaval.glo4003.ws.domain.notification.ModelOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelInventoryObservable {
  private final List<ModelInventoryObserver> assembledObservers = new ArrayList<>();
  private final List<ModelOrderDelayObserver> delayObservers = new ArrayList<>();

  public void register(ModelInventoryObserver observer) {
    assembledObservers.add(observer);
  }

  public void register(ModelOrderDelayObserver observer) {
    delayObservers.add(observer);
  }

  public void notifyModelInStock(Order order) {
    for (ModelInventoryObserver observer : assembledObservers) {
      observer.listenToModelInStock(order);
    }
  }

  public void notifyModelDelay(Order order) {
    for (ModelOrderDelayObserver observer : delayObservers) {
      observer.listenModelOrderDelay(order);
    }
  }
}
