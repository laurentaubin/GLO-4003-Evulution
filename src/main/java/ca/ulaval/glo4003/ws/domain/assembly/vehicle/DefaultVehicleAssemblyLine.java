package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultVehicleAssemblyLine implements VehicleAssemblyLineStrategy {
  private static final Logger LOGGER = LogManager.getLogger();

  private List<Order> orders = new ArrayList<>();
  private final VehicleAssemblyPlanner vehicleAssemblyPlanner;

  public DefaultVehicleAssemblyLine(VehicleAssemblyPlanner vehicleAssemblyPlanner) {
    this.vehicleAssemblyPlanner = vehicleAssemblyPlanner;
  }

  @Override
  public void advance() {
    orders.forEach(Order::advance);
    clearAssembledVehicles();
  }

  @Override
  public void assembleVehicle(Order order) {
    ProductionTime productionTime = vehicleAssemblyPlanner.getProductionTime(order);
    order.setRemainingAssemblyTime(productionTime);
    orders.add(order);
  }

  @Override
  public ProductionTime computeRemainingTimeToProduce(OrderId orderId) {
    return orders.stream()
        .filter(order -> order.getId().equals(orderId))
        .findFirst()
        .get()
        .getRemainingAssemblyTime();
  }

  public List<Order> getCurrentOrders() {
    return orders;
  }

  private void clearAssembledVehicles() {
    orders = orders.stream().filter(order -> !isOrderOver(order)).collect(Collectors.toList());
  }

  private boolean isOrderOver(Order order) {
    if (order.isOver()) {
      LOGGER.info(String.format("Vehicle for order %s assembled", order.getId()));
      return true;
    }
    return false;
  }
}
