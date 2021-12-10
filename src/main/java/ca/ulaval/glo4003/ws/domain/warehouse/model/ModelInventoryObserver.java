package ca.ulaval.glo4003.ws.domain.warehouse.model;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface ModelInventoryObserver {
  void listenToModelInStock(Order order);
}
