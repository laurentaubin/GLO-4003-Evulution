package ca.ulaval.glo4003.ws.api.shared;

import java.time.LocalDate;

public class LocalDateProvider {
  public LocalDate today() {
    return LocalDate.now();
  }
}
