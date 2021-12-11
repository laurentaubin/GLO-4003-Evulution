package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;

public class VehicleFactory {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final ModelRepository modelRepository;

  public VehicleFactory() {
    this(serviceLocator.resolve(ModelRepository.class));
  }

  public VehicleFactory(ModelRepository modelRepository) {
    this.modelRepository = modelRepository;
  }

  public Vehicle create(String model, String color) {
    Model vehicleModel = modelRepository.findByModel(model);
    return new Vehicle(vehicleModel, Color.fromString(color));
  }
}
