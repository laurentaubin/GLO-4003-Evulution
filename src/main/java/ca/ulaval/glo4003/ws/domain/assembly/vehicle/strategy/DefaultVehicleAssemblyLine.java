package ca.ulaval.glo4003.ws.domain.assembly.vehicle.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultVehicleAssemblyLine extends VehicleAssemblyObservable
    implements VehicleAssemblyLineStrategy {
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
    LOGGER.info(String.format("Vehicle assembly order received: %s", order.getId()));
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

  @Override
  public List<Order> getActiveOrders() {
    return orders;
  }

  @Override
  public void shutdown() {
    orders = new ArrayList<>();
  }

  private void clearAssembledVehicles() {
    orders = orders.stream().filter(order -> !isOrderOver(order)).collect(Collectors.toList());
  }

  private boolean isOrderOver(Order order) {
    if (order.isOver()) {
      LOGGER.info(String.format("Vehicle for order %s assembled", order.getId()));
      notifyVehicleAssembled(order);
      return true;
    }
    return false;
  }
}
