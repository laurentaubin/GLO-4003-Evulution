package ca.ulaval.glo4003.ws.domain.transaction.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price {
  private final BigDecimal price;

  public Price(BigDecimal price) {
    this.price = price;
  }

  public Price(int price) {
    this.price = new BigDecimal(price);
  }

  public Price(Integer price) {
    this.price = new BigDecimal(price);
  }

  public Integer toInt() {
    return price.intValue();
  }

  public Double toDouble() {
    return price.doubleValue();
  }

  public Price add(Price amount) {
    return new Price(price.add(amount.price));
  }

  public Price subtract(Price amount) {
    return new Price(price.subtract(amount.price));
  }

  public Price divide(double factor) {
    return new Price(price.divide(BigDecimal.valueOf(factor), RoundingMode.HALF_UP));
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Price)) {
      return false;
    }
    Price object = (Price) o;
    return price.equals(object.price);
  }
}
