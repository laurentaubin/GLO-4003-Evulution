package ca.ulaval.glo4003.ws.domain.assembly.battery.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import java.util.List;

public interface BatteryAssemblyLineStrategy {
  void advance();

  void addOrder(Order order);

  AssemblyTime computeRemainingTimeToProduce(OrderId orderId);

  List<Order> getActiveOrders();

  void shutdown();
}
