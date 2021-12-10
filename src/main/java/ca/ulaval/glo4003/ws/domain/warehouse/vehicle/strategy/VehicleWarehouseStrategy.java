package ca.ulaval.glo4003.ws.domain.warehouse.vehicle.strategy;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.List;

public interface VehicleWarehouseStrategy {

  void addOrder(Order order);

  AssemblyTime computeRemainingTimeToProduce(OrderId orderId);

  List<Order> cancelAllOrders();
}
