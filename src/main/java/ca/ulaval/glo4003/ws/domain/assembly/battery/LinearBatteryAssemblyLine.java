package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

// TODO remove from exclude and write tests
public class LinearBatteryAssemblyLine implements BatteryAssemblyLineStrategy {
  @Override
  public void advance() {
    // Notify observers if assembled
  }

  @Override
  public void assembleBattery(Order order) {}

  @Override
  public int computeRemainingTimeToProduce(OrderId orderId) {
    return 0;
  }
}
