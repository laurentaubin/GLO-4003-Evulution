package ca.ulaval.glo4003.ws.domain.vehicle.exception;

import java.util.Set;

public class ModelNotFoundException extends RuntimeException {
  private final Set<String> models;

  public ModelNotFoundException(Set<String> models) {
    super();
    this.models = models;
  }

  public Set<String> getModels() {
    return models;
  }
}
