package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.UUID;

public class BatteryOrderFactory {
  public BatteryOrder create(String batteryType, ProductionTime productionTime) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new BatteryOrder(orderId, batteryType, productionTime);
  }
}
