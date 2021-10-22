package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.time.LocalDate;

public class Order {
  private final OrderId id;
  private final Model model;
  private final Battery battery;
  private final LocalDate createdAt;
  private final ProductionTime initialProductionTime;
  private ProductionTime remainingAssemblyTime;
  private ProductionTime assemblyDelay;

  public Order(
      OrderId orderId,
      Model model,
      Battery battery,
      LocalDate createdAt,
      ProductionTime initialAssemblyTime) {
    this.id = orderId;
    this.model = model;
    this.battery = battery;
    this.createdAt = createdAt;
    this.initialProductionTime =
        initialAssemblyTime.add(model.getProductionTime()).add(battery.getProductionTime());
    this.remainingAssemblyTime = initialAssemblyTime;
    this.assemblyDelay = new ProductionTime(0);
  }

  public void advance() {
    remainingAssemblyTime = remainingAssemblyTime.subtractWeeks(1);
  }

  public boolean isOver() {
    return remainingAssemblyTime.isOver();
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

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public ProductionTime getRemainingAssemblyTime() {
    return remainingAssemblyTime;
  }

  public void setRemainingAssemblyTime(ProductionTime remainingAssemblyTime) {
    this.remainingAssemblyTime = remainingAssemblyTime;
  }

  public ProductionTime getAssemblyDelay() {
    return assemblyDelay;
  }

  public void addAssemblyDelay(ProductionTime additionalDelay) {
    assemblyDelay = assemblyDelay.add(additionalDelay);
  }

  public LocalDate computeDeliveryDate() {
    return createdAt.plusWeeks(initialProductionTime.inWeeks()).plusWeeks(assemblyDelay.inWeeks());
  }
}
