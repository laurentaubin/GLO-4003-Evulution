package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultVehicleAssemblyLine implements VehicleAssemblyLineStrategy {
  private List<Order> orders = new ArrayList<>();
  private final VehicleAssemblyPlanner vehicleAssemblyPlanner;

  public DefaultVehicleAssemblyLine(VehicleAssemblyPlanner vehicleAssemblyPlanner) {
    this.vehicleAssemblyPlanner = vehicleAssemblyPlanner;
  }

  @Override
  public void advance() {
    orders.forEach(order -> order.advance());
    // TODO: notify vehicle ready for delivery
    clearAssembledVehicles();
  }

  @Override
  public void assembleVehicle(Order order) {
    ProductionTime productionTime = vehicleAssemblyPlanner.getProductionTime(order);
    order.setRemainingProductionTime(productionTime);
    orders.add(order);
  }

  @Override
  public ProductionTime computeRemainingTimeToProduce(OrderId orderId) {
    return orders.stream()
        .filter(order -> order.getId().equals(orderId))
        .findFirst()
        .get()
        .getRemainingProductionTime();
  }

  public List<Order> getCurrentOrders() {
    return orders;
  }

  private void clearAssembledVehicles() {
    orders = orders.stream().filter(order -> !order.isOver()).collect(Collectors.toList());
  }
}
