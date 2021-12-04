package ca.ulaval.glo4003.ws.domain.assembly.vehicle.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.List;

public interface VehicleAssemblyLineStrategy {
  void advance();

  void assembleVehicle(Order order);

  ProductionTime computeRemainingTimeToProduce(OrderId orderId);

  List<Order> getActiveOrders();

  void shutdown();
}
