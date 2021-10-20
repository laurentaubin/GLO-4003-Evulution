package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public enum VehicleAssemblyProductionTime {
  NORMAL(new ProductionTime(1)),
  DELAYED(new ProductionTime(2));

  private final ProductionTime productionTime;

  VehicleAssemblyProductionTime(ProductionTime productionTime) {
    this.productionTime = productionTime;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
