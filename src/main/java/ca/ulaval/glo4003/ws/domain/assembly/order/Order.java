package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;

public class Order {
  private final OrderId id;
  private final Model model;
  private final Battery battery;
  private ProductionTime remainingProductionTime;

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
    remainingProductionTime = remainingProductionTime.subtractWeeks(1);
  }

  public ProductionTime getRemainingProductionTime() {
    return remainingProductionTime;
  }

  public void setRemainingProductionTime(ProductionTime remainingProductionTime) {
    this.remainingProductionTime = remainingProductionTime;
  }

  public boolean isOver() {
    return remainingProductionTime.isOver();
  }
}
