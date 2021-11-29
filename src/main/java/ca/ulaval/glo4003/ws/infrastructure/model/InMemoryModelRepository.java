package ca.ulaval.glo4003.ws.infrastructure.model;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.ModelNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryModelRepository implements ModelRepository {
  private final Map<String, ModelDto> models;
  private final ModelAssembler modelAssembler;

  public InMemoryModelRepository(Map<String, ModelDto> models) {
    this(models, new ModelAssembler());
  }

  public InMemoryModelRepository(Map<String, ModelDto> models, ModelAssembler modelAssembler) {
    this.models = models;
    this.modelAssembler = modelAssembler;
  }

  public Model findByModel(String modelName) {
    if (models.containsKey(modelName.toUpperCase())) {
      return modelAssembler.assembleModel(models.get(modelName.toUpperCase()));
    }
    throw new ModelNotFoundException(models.keySet());
  }

  public Collection<Model> findAllModels() {
    return models.values().stream().map(modelAssembler::assembleModel).collect(Collectors.toList());
  }
}
