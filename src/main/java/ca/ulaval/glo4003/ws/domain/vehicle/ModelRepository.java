package ca.ulaval.glo4003.ws.domain.vehicle;

import java.util.Collection;

public interface ModelRepository {

  Model findByModel(String vehicleModel);

  Collection<Model> findAllModels();
}
