package ca.ulaval.glo4003.ws.domain.battery;

public class Battery {
  public String type;
  public Integer baseNRCANRange;
  public Integer capacity;
  public Integer price;
  public Integer timeToProduce;

  public Battery(
      String type, Integer baseNRCANRange, Integer capacity, Integer price, Integer timeToProduce) {
    this.type = type;
    this.baseNRCANRange = baseNRCANRange;
    this.capacity = capacity;
    this.price = price;
    this.timeToProduce = timeToProduce;
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

  public Integer getPrice() {
    return price;
  }

  public Integer getTimeToProduce() {
    return timeToProduce;
  }
}
