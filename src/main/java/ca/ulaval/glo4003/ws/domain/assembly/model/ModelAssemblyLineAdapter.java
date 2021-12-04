package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

public interface ModelAssemblyLineAdapter {
  AssemblyStatus getAssemblyStatus(OrderId orderId);

  void addOrder(Order order);

  void addOrder(ModelOrder modelOrder);

  void advance();
}
