package ca.ulaval.glo4003.ws.domain.vehicle;

import java.math.BigDecimal;

public class Model {
  private final String name;
  private final String style;
  private final BigDecimal efficiency;
  private final Integer basePrice;
  private final Integer timeToProduce;

  public Model(
      String name, String style, BigDecimal efficiency, Integer basePrice, Integer timeToProduce) {
    this.name = name;
    this.style = style;
    this.efficiency = efficiency;
    this.basePrice = basePrice;
    this.timeToProduce = timeToProduce;
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

  public Integer getTimeToProduce() {
    return timeToProduce;
  }
}
