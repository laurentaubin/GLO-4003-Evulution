package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

public interface ModelAssembledObserver {
  void listenToModelAssembled(Order order);
}
