package ca.ulaval.glo4003.ws.domain.vehicle.battery;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public class Battery {
  public String type;
  public Integer baseNRCANRange;
  public Integer capacity;
  public Integer price;
  public ProductionTime productionTime;

  public Battery(
      String type,
      Integer baseNRCANRange,
      Integer capacity,
      Integer price,
      ProductionTime productionTime) {
    this.type = type;
    this.baseNRCANRange = baseNRCANRange;
    this.capacity = capacity;
    this.price = price;
    this.productionTime = productionTime;
  }

  public String getType() {
    return type;
  }

  public Integer getBaseNRCANRange() {
    return baseNRCANRange;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public Integer getPrice() {
    return price;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
