package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

// TODO remove from exclude and write tests
public class LinearModelAssemblyLine implements ModelAssemblyLineStrategy {
  @Override
  public void advance() {
    // Notify observers if assembled
  }

  @Override
  public void addModel() {}

  @Override
  public int computeRemainingTimeToProduce(OrderId orderId) {
    return 0;
  }
}
