package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.List;

public interface ModelWarehouseStrategy {
  void addOrder(Order order);

  AssemblyTime computeRemainingTimeToProduce(OrderId orderId);

  List<Order> getActiveOrders();
}
