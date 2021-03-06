package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

import java.time.LocalDate;
import java.util.UUID;

public class OrderBuilder {
  private OrderId orderId = new OrderId(UUID.randomUUID().toString());
  private ModelOrder modelOrder = new ModelOrderBuilder().build();
  private BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
  private LocalDate createdAt = LocalDate.now();
  private AssemblyTime initialAssemblyTime = new AssemblyTime(3);

  public OrderBuilder withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public OrderBuilder withModelOrder(ModelOrder modelOrder) {
    this.modelOrder = modelOrder;
    return this;
  }

  public OrderBuilder withBatteryOrder(BatteryOrder batteryOrder) {
    this.batteryOrder = batteryOrder;
    return this;
  }

  public OrderBuilder withCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public OrderBuilder withInitialAssemblyTime(AssemblyTime initialAssemblyTime) {
    this.initialAssemblyTime = initialAssemblyTime;
    return this;
  }

  public OrderBuilder withModelType(String modelType) {
    modelOrder = new ModelOrder(modelOrder.getOrderId(), modelType, modelOrder.getAssemblyTime());
    return this;
  }

  public Order build() {
    return new Order(orderId, modelOrder, batteryOrder, createdAt, initialAssemblyTime);
  }
}
