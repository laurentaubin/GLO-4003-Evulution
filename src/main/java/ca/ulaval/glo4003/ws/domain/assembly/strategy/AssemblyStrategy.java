package ca.ulaval.glo4003.ws.domain.assembly.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

public interface AssemblyStrategy {
  void advance();

  void addOrder(Order order);
}
