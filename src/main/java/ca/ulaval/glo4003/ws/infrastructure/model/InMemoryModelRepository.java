package ca.ulaval.glo4003.ws.infrastructure.model;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.ModelNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;

import java.util.Collection;
import java.util.Map;

public class InMemoryModelRepository implements ModelRepository {
  private final Map<String, Model> models;

  public InMemoryModelRepository(Map<String, Model> models) {
    this.models = models;
  }

  public Model findByModel(String modelName) {
    if (models.containsKey(modelName.toUpperCase())) {
      return models.get(modelName.toUpperCase());
    }
    throw new ModelNotFoundException(models.keySet());
  }

  public Collection<Model> findAllModels() {
    return models.values();
  }
}
