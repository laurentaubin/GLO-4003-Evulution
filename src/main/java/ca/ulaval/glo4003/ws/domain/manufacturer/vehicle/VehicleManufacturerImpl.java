package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.manufacturer.PeriodicManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleManufacturerImpl extends VehicleAssembledObservable
    implements VehicleManufacturer, PeriodicManufacturer {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final Logger LOGGER = LogManager.getLogger();

  private List<Order> orders = new ArrayList<>();
  private final VehicleAssemblyPlanner vehicleAssemblyPlanner;

  public VehicleManufacturerImpl() {
    this(serviceLocator.resolve(VehicleAssemblyPlanner.class));
  }

  public VehicleManufacturerImpl(VehicleAssemblyPlanner vehicleAssemblyPlanner) {
    this.vehicleAssemblyPlanner = vehicleAssemblyPlanner;
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(String.format("Vehicle assembly order received: %s", order.getId()));
    AssemblyTime assemblyTime = vehicleAssemblyPlanner.getAssemblyTime(order);
    if (assemblyTime.equals(VehicleAssemblyProductionTime.DELAYED.getAssemblyTime())) {
      notifyVehicleOrderDelay(order);
    }
    order.setRemainingAssemblyTime(assemblyTime);
    orders.add(order);
  }

  @Override
  public void advanceTime() {
    orders.forEach(Order::advance);
    clearAssembledVehicles();
  }

  @Override
  public void stop() {
    orders.clear();
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    return orders.stream()
        .filter(order -> order.getId().equals(orderId))
        .findFirst()
        .get()
        .getRemainingAssemblyTime();
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
