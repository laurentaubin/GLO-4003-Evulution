package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.math.BigDecimal;

public class ModelBuilder {
  private String name = "a name";
  private String style = "a style";
  private BigDecimal efficiency = BigDecimal.TEN;
  private Price basePrice = new Price(487653);
  private ProductionTime productionTime = new ProductionTime(1);

  public ModelBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ModelBuilder withStyle(String style) {
    this.style = style;
    return this;
  }

  public ModelBuilder withEfficiency(BigDecimal efficiency) {
    this.efficiency = efficiency;
    return this;
  }

  public ModelBuilder withBasePrice(Price basePrice) {
    this.basePrice = basePrice;
    return this;
  }

  public ModelBuilder withProductionTime(ProductionTime productionTime) {
    this.productionTime = productionTime;
    return this;
  }

  public Model build() {
    return new Model(name, style, efficiency, basePrice, productionTime);
  }
}
