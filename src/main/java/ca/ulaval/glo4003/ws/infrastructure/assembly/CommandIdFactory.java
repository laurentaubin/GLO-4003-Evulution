package ca.ulaval.glo4003.ws.infrastructure.assembly;

import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

public class CommandIdFactory {
  public CommandID createFromOrderId(OrderId orderId) {
    return new CommandID(orderId.toString());
  }
}
