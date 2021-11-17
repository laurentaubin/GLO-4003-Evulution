package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.ModelOrder;

public interface ModelAssemblyLineAdapter {
  AssemblyStatus getAssemblyStatus(OrderId orderId);

  void addOrder(Order order);

  void addOrder(ModelOrder modelOrder);

  void advance();
}
