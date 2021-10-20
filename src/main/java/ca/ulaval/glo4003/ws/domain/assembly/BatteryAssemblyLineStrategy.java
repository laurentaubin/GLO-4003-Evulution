package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public interface BatteryAssemblyLineStrategy {
  void advance();

  void addOrder(Order order);

  ProductionTime computeRemainingTimeToProduce(OrderId orderId);
}
