package ca.ulaval.glo4003.ws.domain.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidLocationException;

public enum Location {
  VACHON("Vachon"),
  PEPS("PEPS"),
  DESJARDINS("Desjardins"),
  FRABRIQUE("Frabrique");

  private final String campusLocation;

  Location(String campusLocation) {
    this.campusLocation = campusLocation;
  }

  public static Location fromString(String value) {
    try {
      return Location.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidLocationException();
    }
  }

  public String getCampusLocation() {
    return campusLocation;
  }
}
