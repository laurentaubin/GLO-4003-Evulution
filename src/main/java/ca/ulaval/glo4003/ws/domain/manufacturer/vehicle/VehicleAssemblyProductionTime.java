package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public enum VehicleAssemblyProductionTime {
  NORMAL(new AssemblyTime(1)),
  DELAYED(new AssemblyTime(2));

  private final AssemblyTime assemblyTime;

  VehicleAssemblyProductionTime(AssemblyTime assemblyTime) {
    this.assemblyTime = assemblyTime;
  }

  public AssemblyTime getAssemblyTime() {
    return assemblyTime;
  }
}
