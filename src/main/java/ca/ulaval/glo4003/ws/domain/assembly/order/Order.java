package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;

// TODO remove from exclude and write tests
public class Order {
  private OrderId id;

  private Model model;
  private Battery battery;

  public Order(OrderId orderId, Model model, Battery battery) {
    this.id = orderId;
    this.model = model;
    this.battery = battery;
  }

  public OrderId getId() {
    return id;
  }

  public Model getModel() {
    return model;
  }

  public Battery getBattery() {
    return battery;
  }
}
