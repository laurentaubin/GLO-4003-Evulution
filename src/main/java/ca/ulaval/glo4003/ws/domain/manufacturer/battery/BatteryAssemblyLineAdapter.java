package ca.ulaval.glo4003.ws.domain.manufacturer.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;

public interface BatteryAssemblyLineAdapter {
  AssemblyStatus getAssemblyStatus(OrderId orderId);

  void addOrder(BatteryOrder batteryOrder);

  void advance();
}
