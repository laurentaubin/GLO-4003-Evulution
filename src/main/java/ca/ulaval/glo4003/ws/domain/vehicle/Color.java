package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.InvalidVehicleColorException;

public enum Color {
  WHITE;

  public static Color fromString(String value) {
    try {
      return Color.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidVehicleColorException();
    }
  }
}
