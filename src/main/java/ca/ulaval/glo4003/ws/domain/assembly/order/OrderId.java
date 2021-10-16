package ca.ulaval.glo4003.ws.domain.assembly.order;

// TODO remove from exclude and write tests
public class OrderId {
  private final String id;

  public OrderId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }
}
