package ca.ulaval.glo4003.ws.domain.warehouse;

import ca.ulaval.glo4003.ws.domain.notification.OrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderDelayObservable {
  private final List<OrderDelayObserver> shutdownObservers = new ArrayList<>();

  public void register(OrderDelayObserver observer) {
    shutdownObservers.add(observer);
  }

  public void notifyOrderDelay(List<Order> orders) {
    for (OrderDelayObserver observer : shutdownObservers) {
      observer.listenToOrderDelay(orders);
    }
  }
}
