package ca.ulaval.glo4003.ws.domain.assembly.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import java.util.List;

public interface AssemblyStrategy {
  void advance();

  void addOrder(Order order);

  List<Order> getActiveOrders();

  void reactivate();

  void shutdown();
}
