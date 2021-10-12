package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

public interface ModelAssemblyLineStrategy {
  void advance();

  void addModel();

  int computeRemainingTimeToProduce(OrderId orderId);
}