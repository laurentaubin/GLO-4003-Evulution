package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

public interface BatteryAssemblyLineStrategy {
  void advance();

  void assembleBattery(Order order);

  int computeRemainingTimeToProduce(OrderId orderId);
}
