package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public interface VehicleManufacturer {
  void addOrder(Order order);

  AssemblyTime computeRemainingTimeToProduce(OrderId orderId);
}
