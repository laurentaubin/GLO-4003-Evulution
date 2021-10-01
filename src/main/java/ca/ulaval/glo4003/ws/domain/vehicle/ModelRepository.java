package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.transaction.Model;

public interface ModelRepository {

  Model findByModel(String vehicleModel);
}
