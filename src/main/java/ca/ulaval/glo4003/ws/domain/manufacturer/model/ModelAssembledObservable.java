package ca.ulaval.glo4003.ws.domain.manufacturer.model;

import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import java.util.ArrayList;
import java.util.List;

public class ModelAssembledObservable {
  private final List<ModelAssembledObserver> assembledObservers = new ArrayList<>();

  public void register(ModelAssembledObserver observer) {
    assembledObservers.add(observer);
  }

  public void notifyModelAssembled(ModelOrder modelOrder) {
    for (ModelAssembledObserver observer : assembledObservers) {
      observer.listenToModelAssembled(modelOrder);
    }
  }
}
