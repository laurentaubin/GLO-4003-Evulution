package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.time.LocalDate;
import java.util.UUID;

public class OrderBuilder {
  private OrderId orderId = new OrderId(UUID.randomUUID().toString());
  private ModelOrder modelOrder = new ModelOrderBuilder().build();
  private BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
  private LocalDate createdAt = LocalDate.now();
  private ProductionTime initialProductionTime = new ProductionTime(3);

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

  public OrderBuilder withInitialProductionTime(ProductionTime initialProductionTime) {
    this.initialProductionTime = initialProductionTime;
    return this;
  }

  public Order build() {
    return new Order(orderId, modelOrder, batteryOrder, createdAt, initialProductionTime);
  }
}
