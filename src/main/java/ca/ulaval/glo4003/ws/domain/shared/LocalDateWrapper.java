package ca.ulaval.glo4003.ws.domain.shared;

import java.time.LocalDate;

public class LocalDateWrapper {
  public LocalDate today() {
    return LocalDate.now();
  }
}
