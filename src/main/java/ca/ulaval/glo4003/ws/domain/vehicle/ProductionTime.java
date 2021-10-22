package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.InvalidOperationException;
import java.time.Period;

public class ProductionTime {
  Period productionTime;

  public ProductionTime(int weeks) {
    this.productionTime = Period.ofWeeks(weeks);
  }

  public int inWeeks() {
    return productionTime.getDays() / 7;
  }

  public ProductionTime subtractWeeks(int weeks) {
    int newProductionTime = (productionTime.getDays() / 7) - weeks;
    if (newProductionTime < 0) {
      throw new InvalidOperationException("Production time cannot be negative.");
    }
    return new ProductionTime(newProductionTime);
  }

  public boolean isOver() {
    return productionTime.isZero();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ProductionTime)) {
      return false;
    }
    ProductionTime object = (ProductionTime) o;
    return productionTime.equals(object.productionTime);
  }

  public ProductionTime subtract(ProductionTime other) {
    int newProductionTime = (this.productionTime.getDays() - other.productionTime.getDays()) / 7;
    if (newProductionTime < 0) {
      throw new IllegalArgumentException("Production time cannot be negative.");
    }
    return new ProductionTime(newProductionTime);
  }

  public ProductionTime add(ProductionTime other) {
    int newProductionTime = (this.productionTime.getDays() + other.productionTime.getDays()) / 7;
    return new ProductionTime(newProductionTime);
  }
}
