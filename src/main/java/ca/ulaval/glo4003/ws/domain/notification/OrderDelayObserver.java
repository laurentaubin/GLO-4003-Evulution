package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import java.util.List;

public interface OrderDelayObserver {
  void listenToOrderDelay(List<Order> orders);
}
