package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;

import java.time.LocalDate;

public class OrderBuilder {
  private OrderId orderId = new OrderId("anOrderId");
  private Model model = new ModelBuilder().build();
  private Battery battery = new BatteryBuilder().build();
  private LocalDate createdAt = LocalDate.now();
  private ProductionTime initialProductionTime = new ProductionTime(3);

  public OrderBuilder withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public OrderBuilder withModel(Model model) {
    this.model = model;
    return this;
  }

  public OrderBuilder withBattery(Battery battery) {
    this.battery = battery;
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
    return new Order(orderId, model, battery, createdAt, initialProductionTime);
  }
}
