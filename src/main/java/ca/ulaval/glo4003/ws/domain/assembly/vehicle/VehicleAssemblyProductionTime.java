package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

public enum VehicleAssemblyProductionTime {
  NORMAL(7),
  DELAYED(14);

  private int productionTime;

  VehicleAssemblyProductionTime(int productionTime) {
    this.productionTime = productionTime;
  }

  public int getProductionTime() {
    return productionTime;
  }
}
