package ca.ulaval.glo4003.ws.domain.vehicle.model;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.math.BigDecimal;

public class Model {
  private final String name;
  private final String style;
  private final BigDecimal efficiency;
  private final Integer basePrice;
  private final ProductionTime productionTime;

  public Model(
      String name,
      String style,
      BigDecimal efficiency,
      Integer basePrice,
      ProductionTime productionTime) {
    this.name = name;
    this.style = style;
    this.efficiency = efficiency;
    this.basePrice = basePrice;
    this.productionTime = productionTime;
  }

  public BigDecimal getEfficiency() {
    return efficiency;
  }

  public String getName() {
    return name;
  }

  public Integer getBasePrice() {
    return basePrice;
  }

  public String getStyle() {
    return style;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
