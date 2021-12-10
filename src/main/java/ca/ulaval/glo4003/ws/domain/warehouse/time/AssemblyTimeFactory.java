package ca.ulaval.glo4003.ws.domain.warehouse.time;

public class AssemblyTimeFactory {
  public AssemblyTime create(int weeks) {
    return new AssemblyTime(weeks);
  }
}
