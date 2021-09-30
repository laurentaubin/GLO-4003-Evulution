package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidFrequencyException;

public enum Frequency {
  MONTHLY("monthly"),
  BIWEEKLY("biweekly"),
  WEEKLY("weekly");

  private String frequency;

  Frequency(String frequency) {
    this.frequency = frequency;
  }

  public static Frequency fromString(String value) {
    try {
      return Frequency.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidFrequencyException();
    }
  }

  public String getFrequency() {
    return frequency;
  }
}
