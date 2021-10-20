package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;

public class Order {
  private OrderId id;
  private Model model;
  private Battery battery;
  private Integer remainingProductionTime;

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

  public void advance() {
    remainingProductionTime = remainingProductionTime - 1;
  }

  public int getRemainingProductionTime() {
    return remainingProductionTime;
  }

  public void setRemainingProductionTime(int remainingProductionTime) {
    this.remainingProductionTime = remainingProductionTime;
  }
}
