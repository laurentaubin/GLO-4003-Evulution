package ca.ulaval.glo4003.ws.domain.warehouse.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.ReactivateObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.ShutdownObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.OrderDelayObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryInventoryObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.VehicleInventoryObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.VehicleWarehouseStrategy;
import java.util.ArrayList;
import java.util.List;

public class LinearWarehouseStrategy extends OrderDelayObservable
    implements WarehouseStrategy,
        ModelInventoryObserver,
        BatteryInventoryObserver,
        VehicleInventoryObserver,
        ShutdownObserver,
        ReactivateObserver {
  private List<Order> ordersNeedingNewBattery = new ArrayList<>();

  private final ModelWarehouseStrategy modelWarehouseStrategy;
  private final BatteryWarehouseStrategy batteryWarehouseStrategy;
  private final VehicleWarehouseStrategy vehicleWarehouseStrategy;
  private final OrderRepository orderRepository;

  public LinearWarehouseStrategy(
      ModelWarehouseStrategy modelWarehouseStrategy,
      BatteryWarehouseStrategy batteryWarehouseStrategy,
      VehicleWarehouseStrategy vehicleWarehouseStrategy,
      OrderRepository orderRepository) {
    this.modelWarehouseStrategy = modelWarehouseStrategy;
    this.batteryWarehouseStrategy = batteryWarehouseStrategy;
    this.vehicleWarehouseStrategy = vehicleWarehouseStrategy;
    this.orderRepository = orderRepository;
  }

  @Override
  public void addOrder(Order order) {
    modelWarehouseStrategy.addOrder(order);
  }

  @Override
  public void listenToModelInStock(Order order) {
    batteryWarehouseStrategy.addOrder(order);
  }

  @Override
  public void listenToVehicleInStock(Order order) {
    order.setIsReadyForDelivery(true);
    orderRepository.save(order);
  }

  @Override
  public void listenToBatteryInStock(Order order) {
    vehicleWarehouseStrategy.addOrder(order);
  }

  @Override
  public void listenToAssemblyShutdown() {
    ordersNeedingNewBattery = collectOrdersToReorderBattery();
    notifyOrderDelay(collectAllActiveOrders());
  }

  @Override
  public void listenToAssemblyReactivation() {
    for (Order order : ordersNeedingNewBattery) {
      batteryWarehouseStrategy.addOrder(order);
    }
    ordersNeedingNewBattery = new ArrayList<>();
  }

  private List<Order> collectOrdersToReorderBattery() {
    List<Order> cancelledOrder = new ArrayList<>();
    cancelledOrder.addAll(vehicleWarehouseStrategy.cancelAllOrders());
    cancelledOrder.addAll(batteryWarehouseStrategy.cancelAllOrders());
    List<Order> ordersReadyForDelivery = orderRepository.findAllCompletedOrders();
    updateAllOrdersReadyForDeliveryAsNotReadyForDelivery(ordersReadyForDelivery);
    cancelledOrder.addAll(ordersReadyForDelivery);
    return cancelledOrder;
  }

  private List<Order> collectAllActiveOrders() {
    List<Order> activeOrders = new ArrayList<>();
    activeOrders.addAll(ordersNeedingNewBattery);
    activeOrders.addAll(modelWarehouseStrategy.getActiveOrders());
    return activeOrders;
  }

  private void updateAllOrdersReadyForDeliveryAsNotReadyForDelivery(
      List<Order> ordersReadyForDelivery) {
    for (Order order : ordersReadyForDelivery) {
      order.setIsReadyForDelivery(false);
      orderRepository.save(order);
    }
  }
}
