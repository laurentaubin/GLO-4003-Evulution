package ca.ulaval.glo4003.ws.infrastructure.warehouse;

import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;

import java.util.HashMap;
import java.util.Map;

public class CommandIdFactory {
  private final Map<OrderId, CommandID> orderIdToCommandID = new HashMap<>();

  public CommandID getOrCreateFromOrderId(OrderId orderId) {
    if (orderIdToCommandID.containsKey(orderId)) return orderIdToCommandID.get(orderId);
    return createCommandId(orderId);
  }

  private CommandID createCommandId(OrderId orderId) {
    CommandID commandId = new CommandID(orderId.toString());
    orderIdToCommandID.put(orderId, commandId);
    return commandId;
  }
}
