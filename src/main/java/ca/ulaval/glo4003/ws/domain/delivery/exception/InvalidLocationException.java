package ca.ulaval.glo4003.ws.domain.delivery.exception;

import java.util.Set;

public class InvalidLocationException extends RuntimeException {
  private final Set<String> locations;

  public InvalidLocationException(Set<String> locations) {
    super();
    this.locations = locations;
  }

  public Set<String> getLocations() {
    return locations;
  }
}
