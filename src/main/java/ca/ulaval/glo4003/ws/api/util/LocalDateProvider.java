package ca.ulaval.glo4003.ws.api.util;

import java.time.LocalDate;

public class LocalDateProvider {
  public LocalDate today() {
    return LocalDate.now();
  }
}
