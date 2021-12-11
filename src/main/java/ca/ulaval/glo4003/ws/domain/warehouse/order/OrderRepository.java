package ca.ulaval.glo4003.ws.domain.warehouse.order;

import java.util.List;

public interface OrderRepository {
  void save(Order order);

  void remove(Order order);

  List<Order> findAllCompletedOrders();
}
