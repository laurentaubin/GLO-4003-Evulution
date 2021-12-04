package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

public interface BatteryAssemblyLineAdapter {
  AssemblyStatus getAssemblyStatus(OrderId orderId);

  void addOrder(Order order);

  void advance();
}
