package ca.ulaval.glo4003.ws.domain.shared;

import java.time.LocalDate;

public class LocalDateProvider {
  private final LocalDateWrapper localDateWrapper;
  private int numberOfAdvances = 0;

  public LocalDateProvider(LocalDateWrapper localDateWrapper) {
    this.localDateWrapper = localDateWrapper;
  }

  public LocalDate today() {
    return localDateWrapper.today().plusWeeks(numberOfAdvances);
  }

  public void advance() {
    numberOfAdvances++;
  }
}
