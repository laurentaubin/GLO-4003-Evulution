package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

import java.time.LocalDate;
import java.util.UUID;

public class OrderFixture {
  private OrderId orderId = new OrderId(UUID.randomUUID().toString());
  private ModelOrder modelOrder = new ModelOrderFixture().build();
  private BatteryOrder batteryOrder = new BatteryOrderFixture().build();
  private LocalDate createdAt = LocalDate.now();
  private AssemblyTime initialAssemblyTime = new AssemblyTime(3);

  public OrderFixture withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public OrderFixture withModelOrder(ModelOrder modelOrder) {
    this.modelOrder = modelOrder;
    return this;
  }

  public OrderFixture withBatteryOrder(BatteryOrder batteryOrder) {
    this.batteryOrder = batteryOrder;
    return this;
  }

  public OrderFixture withCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public OrderFixture withInitialAssemblyTime(AssemblyTime initialAssemblyTime) {
    this.initialAssemblyTime = initialAssemblyTime;
    return this;
  }

  public Order build() {
    return new Order(orderId, modelOrder, batteryOrder, createdAt, initialAssemblyTime);
  }
}
