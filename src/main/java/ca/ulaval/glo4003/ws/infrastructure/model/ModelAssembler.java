package ca.ulaval.glo4003.ws.infrastructure.model;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;

public class ModelAssembler {

  public Model assembleModel(ModelDto modelDto) {
    return new Model(
        modelDto.name.toUpperCase(),
        modelDto.style,
        modelDto.efficiencyEquivalenceRate,
        new Price(modelDto.basePrice),
        new ProductionTime(modelDto.productionTime));
  }
}
