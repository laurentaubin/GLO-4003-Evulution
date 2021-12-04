package ca.ulaval.glo4003.ws.domain.production;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.ProductionShutdownObserver;
import java.util.ArrayList;
import java.util.List;

public class ProductionLineShutdownObservable {
  private final List<ProductionShutdownObserver> shutdownObservers = new ArrayList<>();

  public void register(ProductionShutdownObserver observer) {
    shutdownObservers.add(observer);
  }

  public void notifyProductionShutdown(List<Order> orders) {
    for (ProductionShutdownObserver observer : shutdownObservers) {
      observer.listenProductionLineShutdown(orders);
    }
  }
}
