package ca.ulaval.glo4003.ws.domain.warehouse.strategy;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface WarehouseStrategy {
  void addOrder(Order order);
}
