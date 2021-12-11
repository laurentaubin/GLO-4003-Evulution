package ca.ulaval.glo4003.ws.domain.warehouse.model;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import java.util.List;

public interface ModelWarehouseStrategy {
  void addOrder(Order order);

  List<Order> getActiveOrders();
}
