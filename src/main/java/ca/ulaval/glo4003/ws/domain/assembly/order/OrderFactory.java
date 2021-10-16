package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class OrderFactory {
  public Order create(Transaction transaction) {
    OrderId orderId = new OrderId(transaction.getId().toString());
    return new Order(
        orderId, transaction.getVehicle().getModel(), transaction.getVehicle().getBattery());
  }
}
