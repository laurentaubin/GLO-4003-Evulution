package ca.ulaval.glo4003.ws.infrastructure;

import ca.ulaval.glo4003.ws.domain.transaction.Model;
import ca.ulaval.glo4003.ws.domain.transaction.exception.ModelNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;
import java.util.Map;

public class InMemoryModelRepository implements ModelRepository {
  private final Map<String, Model> models;

  public InMemoryModelRepository(Map<String, Model> models) {
    this.models = models;
  }

  public Model findByModel(String modelName) {
    if (!models.containsKey(modelName)) {
      throw new ModelNotFoundException();
    }
    return models.get(modelName);
  }
}