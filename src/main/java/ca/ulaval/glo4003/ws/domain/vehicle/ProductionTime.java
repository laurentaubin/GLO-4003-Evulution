package ca.ulaval.glo4003.ws.domain.vehicle;

import java.time.Period;

public class ProductionTime {
  Period productionTime;

  public ProductionTime(int weeks) {
    this.productionTime = Period.ofWeeks(weeks);
  }

  public int inWeeks() {
    return productionTime.getDays() / 7;
  }

  public void subtractWeeks(int weeks) {
    int newProductionTime = (productionTime.getDays() / 7) - weeks;
    if (newProductionTime < 0) {
      throw new IllegalArgumentException("Production time cannot be negative.");
    }
    this.productionTime = Period.ofWeeks(newProductionTime);
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
}
