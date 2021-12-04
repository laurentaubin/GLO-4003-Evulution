package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.time.LocalDate;

public class Order {
  private final OrderId id;
  private final ModelOrder modelOrder;
  private final BatteryOrder batteryOrder;
  private final LocalDate createdAt;
  private final ProductionTime initialProductionTime;
  private ProductionTime remainingAssemblyTime;
  private ProductionTime assemblyDelay;
  private boolean isReadyForDelivery = false;

  public Order(
      OrderId orderId,
      ModelOrder model,
      BatteryOrder battery,
      LocalDate createdAt,
      ProductionTime initialAssemblyTime) {
    this.id = orderId;
    this.modelOrder = model;
    this.batteryOrder = battery;
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

  public ModelOrder getModelOrder() {
    return modelOrder;
  }

  public BatteryOrder getBatteryOrder() {
    return batteryOrder;
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

  public boolean isReadyForDelivery() {
    return isReadyForDelivery;
  }

  public void setIsReadyForDelivery(boolean isReadyForDelivery) {
    this.isReadyForDelivery = isReadyForDelivery;
  }

  public boolean isRelatedToTransaction(TransactionId transactionId) {
    return id.toString().equals(transactionId.toString());
  }
}
