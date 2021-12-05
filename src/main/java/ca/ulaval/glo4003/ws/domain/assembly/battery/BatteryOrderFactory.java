package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import java.util.UUID;

public class BatteryOrderFactory {
  public BatteryOrder create(String batteryType, AssemblyTime assemblyTime) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new BatteryOrder(orderId, batteryType, assemblyTime);
  }
}
