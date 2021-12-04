package ca.ulaval.glo4003.ws.domain.vehicle.model;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.math.BigDecimal;

public class Model {
  private final String name;
  private final String style;
  private final BigDecimal efficiency;
  private final Price basePrice;
  private final ProductionTime productionTime;

  public Model(
      String name,
      String style,
      BigDecimal efficiency,
      Price basePrice,
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

  public Price getPrice() {
    return basePrice;
  }

  public String getStyle() {
    return style;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
