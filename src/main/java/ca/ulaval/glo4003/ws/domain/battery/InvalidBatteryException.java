package ca.ulaval.glo4003.ws.domain.battery;

import java.util.Set;

public class InvalidBatteryException extends RuntimeException {
  private final Set<String> batteryTypes;

  public InvalidBatteryException(Set<String> batteryTypes) {
    this.batteryTypes = batteryTypes;
  }

  public Set<String> getBatteryTypes() {
    return batteryTypes;
  }
}
