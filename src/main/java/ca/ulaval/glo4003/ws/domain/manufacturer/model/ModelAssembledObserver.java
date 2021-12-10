package ca.ulaval.glo4003.ws.domain.manufacturer.model;

import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;

public interface ModelAssembledObserver {
  void listenToModelAssembled(ModelOrder modelOrder);
}
