package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import ca.ulaval.glo4003.ws.domain.assembly.*;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LinearAssemblyStrategy
    implements AssemblyStrategy,
        ModelAssembledObserver,
        BatteryAssembledObserver,
        VehicleAssembledObserver {
  private boolean isRunning = true;
  private List<Order> stoppedOrders = new ArrayList<>();

  private final ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  private final BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;
  private final VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy;
  private final OrderRepository orderRepository;

  public LinearAssemblyStrategy(
      ModelAssemblyLineStrategy modelAssemblyLineStrategy,
      BatteryAssemblyLineStrategy batteryAssemblyLineStrategy,
      VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy,
      OrderRepository orderRepository) {
    this.modelAssemblyLineStrategy = modelAssemblyLineStrategy;
    this.batteryAssemblyLineStrategy = batteryAssemblyLineStrategy;
    this.vehicleAssemblyLineStrategy = vehicleAssemblyLineStrategy;
    this.orderRepository = orderRepository;
  }

  @Override
  public void addOrder(Order order) {
    modelAssemblyLineStrategy.addOrder(order);
  }

  @Override
  public List<Order> getActiveOrders() {
    List<Order> activeOrders = new ArrayList<>();
    // TODO Remove duplicate in model and battery orders
    Stream.of(
            modelAssemblyLineStrategy.getActiveOrders(),
            batteryAssemblyLineStrategy.getActiveOrders(),
            vehicleAssemblyLineStrategy.getActiveOrders(),
            orderRepository.findAllCompletedOrders())
        .forEach(activeOrders::addAll);

    return activeOrders;
  }

  @Override
  public void reactivate() {
    if (isRunning) {
      return;
    }
    for (Order order : stoppedOrders) {
      batteryAssemblyLineStrategy.addOrder(order);
    }
    stoppedOrders = new ArrayList<>();
    isRunning = true;
  }

  @Override
  public void shutdown() {
    isRunning = false;
    stoppedOrders.addAll(vehicleAssemblyLineStrategy.getActiveOrders());
    stoppedOrders.addAll(batteryAssemblyLineStrategy.getActiveOrders());
    List<Order> ordersReadyForDelivery = orderRepository.findAllCompletedOrders();
    updateAllOrdersReadyForDeliveryAsNotReadyForDelivery(ordersReadyForDelivery);
    stoppedOrders.addAll(ordersReadyForDelivery);

    vehicleAssemblyLineStrategy.shutdown();
    batteryAssemblyLineStrategy.shutdown();
  }

  @Override
  public void advance() {
    vehicleAssemblyLineStrategy.advance();
    batteryAssemblyLineStrategy.advance();
    modelAssemblyLineStrategy.advance();
  }

  @Override
  public void listenToBatteryAssembled(Order order) {
    vehicleAssemblyLineStrategy.assembleVehicle(order);
  }

  @Override
  public void listenToModelAssembled(Order order) {
    batteryAssemblyLineStrategy.addOrder(order);
  }

  private void updateAllOrdersReadyForDeliveryAsNotReadyForDelivery(
      List<Order> ordersReadyForDelivery) {
    for (Order order : ordersReadyForDelivery) {
      order.setIsReadyForDelivery(false);
      orderRepository.save(order);
    }
  }

  @Override
  public void listenToVehicleAssembled(Order order) {
    order.setIsReadyForDelivery(true);
    orderRepository.save(order);
  }
}
