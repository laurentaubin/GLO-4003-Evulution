package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;

public class BatteryFixture {
  private String type = "a type";
  private int baseNRCANRange = 423;
  private int capacity = 5643;
  private Price price = new Price(200092);
  private ProductionTime productionTime = new ProductionTime(1);

  public BatteryFixture withType(String type) {
    this.type = type;
    return this;
  }

  public BatteryFixture withBaseNRCANRange(int baseNRCANRange) {
    this.baseNRCANRange = baseNRCANRange;
    return this;
  }

  public BatteryFixture withCapacity(int capacity) {
    this.capacity = capacity;
    return this;
  }

  public BatteryFixture withPrice(Price price) {
    this.price = price;
    return this;
  }

  public BatteryFixture withProductionTime(ProductionTime productionTime) {
    this.productionTime = productionTime;
    return this;
  }

  public Battery build() {
    return new Battery(type, baseNRCANRange, capacity, price, productionTime);
  }
}
