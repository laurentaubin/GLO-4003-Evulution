package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;

public class VehicleFactory {
  private final ModelRepository modelRepository;

  public VehicleFactory(ModelRepository modelRepository) {
    this.modelRepository = modelRepository;
  }

  public Vehicle create(String model, String color) {
    Model vehicleModel = modelRepository.findByModel(model);
    return new Vehicle(vehicleModel, Color.fromString(color));
  }
}
