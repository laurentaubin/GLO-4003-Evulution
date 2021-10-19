package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

public interface ModelAssemblyDelayObserver {
  void listenModelAssemblyDelay(Order order);
}
