package ca.ulaval.glo4003.ws.domain.manufacturer.model;

import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;

public interface ModelAssemblyLineAdapter {
  AssemblyStatus getAssemblyStatus(OrderId orderId);

  void addOrder(ModelOrder modelOrder);

  void advance();
}
