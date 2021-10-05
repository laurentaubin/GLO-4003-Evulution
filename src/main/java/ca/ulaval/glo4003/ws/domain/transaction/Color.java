package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidVehicleColorException;

public enum Color {
  WHITE("white");

  private final String color;

  Color(String color) {
    this.color = color;
  }

  public static Color fromString(String value) {
    try {
      return Color.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidVehicleColorException();
    }
  }
}
