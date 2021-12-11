package ca.ulaval.glo4003.ws.domain.warehouse.vehicle.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleAssembledObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.VehicleInventoryObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.VehicleWarehouseStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultVehicleWarehouseStrategy extends VehicleInventoryObservable
    implements VehicleWarehouseStrategy, VehicleAssembledObserver {

  private List<Order> orders = new ArrayList<>();
  private final VehicleManufacturer vehicleManufacturer;

  public DefaultVehicleWarehouseStrategy(VehicleManufacturer vehicleManufacturer) {
    this.vehicleManufacturer = vehicleManufacturer;
  }

  @Override
  public void addOrder(Order order) {
    vehicleManufacturer.addOrder(order);
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    return vehicleManufacturer.computeRemainingTimeToProduce(orderId);
  }

  @Override
  public List<Order> cancelAllOrders() {
    List<Order> cancelledOrders = new ArrayList<>(this.orders);
    orders.clear();
    return cancelledOrders;
  }

  @Override
  public void listenToVehicleAssembled(Order assembledOrder) {
    orders =
        orders.stream()
            .filter(order -> !order.getId().equals(assembledOrder.getId()))
            .collect(Collectors.toList());
    notifyVehicleInStock(assembledOrder);
  }
}
