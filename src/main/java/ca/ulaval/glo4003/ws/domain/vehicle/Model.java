package ca.ulaval.glo4003.ws.domain.vehicle;

import java.math.BigDecimal;

public class Model {
  private String name;
  private String style;
  private BigDecimal efficiency;
  private Integer basePrice;
  private String timeToProduce;

  public Model(
      String name, String style, BigDecimal efficiency, Integer basePrice, String timeToProduce) {
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

  public String getTimeToProduce() {
    return timeToProduce;
  }
}
