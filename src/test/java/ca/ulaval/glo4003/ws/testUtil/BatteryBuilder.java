package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;

public class BatteryBuilder {
  private String type = "a type";
  private int baseNRCANRange = 423;
  private int capacity = 5643;
  private int price = 200092;
  private ProductionTime productionTime = new ProductionTime(1);

  public BatteryBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public BatteryBuilder withBaseNRCANRange(int baseNRCANRange) {
    this.baseNRCANRange = baseNRCANRange;
    return this;
  }

  public BatteryBuilder withCapacity(int capacity) {
    this.capacity = capacity;
    return this;
  }

  public BatteryBuilder withPrice(int price) {
    this.price = price;
    return this;
  }

  public BatteryBuilder withProductionTime(ProductionTime productionTime) {
    this.productionTime = productionTime;
    return this;
  }

  public Battery build() {
    return new Battery(type, baseNRCANRange, capacity, price, productionTime);
  }
}

