package ca.ulaval.glo4003.ws.domain.assembly.model;

public interface ModelInventory {
  void addOne(String modelType);

  boolean isInStock(String modelType);

  void removeOne(String modelType);
}
