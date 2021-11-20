package ca.ulaval.glo4003.ws.domain.vehicle.battery;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public class Battery {
  private final String type;
  private final Integer baseNRCANRange;
  private final Integer capacity;
  private final Price price;
  private final ProductionTime productionTime;

  public Battery(
      String type,
      Integer baseNRCANRange,
      Integer capacity,
      Price price,
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

  public Price getPrice() {
    return price;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
