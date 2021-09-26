package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.domain.transaction.Color;
import ca.ulaval.glo4003.ws.domain.transaction.Model;
import ca.ulaval.glo4003.ws.domain.transaction.Vehicle;

public class VehicleRequestAssembler {

  public Vehicle create(VehicleRequest vehicleRequest) {
    return new Vehicle(
        Model.fromString(vehicleRequest.getModel()), new Color(vehicleRequest.getColor()));
  }
}
