package ca.ulaval.glo4003.ws.domain.warehouse.order;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.time.LocalDate;

public class Order {
  private final OrderId id;
  private final ModelOrder modelOrder;
  private final BatteryOrder batteryOrder;
  private final LocalDate createdAt;
  private final AssemblyTime initialAssemblyTime;
  private AssemblyTime remainingAssemblyTime;
  private AssemblyTime assemblyDelay;
  private boolean isReadyForDelivery = false;

  public Order(
      OrderId orderId,
      ModelOrder model,
      BatteryOrder battery,
      LocalDate createdAt,
      AssemblyTime initialAssemblyTime) {
    this.id = orderId;
    this.modelOrder = model;
    this.batteryOrder = battery;
    this.createdAt = createdAt;
    this.initialAssemblyTime =
        initialAssemblyTime.add(model.getAssemblyTime()).add(battery.getAssemblyTime());
    this.remainingAssemblyTime = initialAssemblyTime;
    this.assemblyDelay = new AssemblyTime(0);
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

  public AssemblyTime getRemainingAssemblyTime() {
    return remainingAssemblyTime;
  }

  public void setRemainingAssemblyTime(AssemblyTime remainingAssemblyTime) {
    this.remainingAssemblyTime = remainingAssemblyTime;
  }

  public AssemblyTime getAssemblyDelay() {
    return assemblyDelay;
  }

  public void addAssemblyDelay(AssemblyTime additionalDelay) {
    assemblyDelay = assemblyDelay.add(additionalDelay);
  }

  public LocalDate computeDeliveryDate() {
    return createdAt.plusWeeks(initialAssemblyTime.inWeeks()).plusWeeks(assemblyDelay.inWeeks());
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
