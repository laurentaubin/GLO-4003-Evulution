package ca.ulaval.glo4003.ws.domain.assembly.order;

import java.util.Objects;

public class OrderId {
  private final String id;

  public OrderId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof OrderId)) {
      return false;
    }
    OrderId orderId = (OrderId) o;
    return id.equals(orderId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
