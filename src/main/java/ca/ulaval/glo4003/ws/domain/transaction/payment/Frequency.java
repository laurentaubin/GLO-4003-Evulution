package ca.ulaval.glo4003.ws.domain.transaction.payment;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidFrequencyException;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Frequency {
  MONTHLY("monthly", 12),
  BIWEEKLY("biweekly", 104),
  WEEKLY("weekly", 52);

  private final String frequency;
  private final Integer paymentsPerYearInWeeks;

  Frequency(String frequency, Integer paymentsPerYearInWeeks) {
    this.frequency = frequency;
    this.paymentsPerYearInWeeks = paymentsPerYearInWeeks;
  }

  public static Frequency fromString(String value) {
    try {
      return Frequency.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidFrequencyException(
          Arrays.stream(Frequency.values())
              .map(Frequency::getFrequency)
              .collect(Collectors.toSet()));
    }
  }

  public String getFrequency() {
    return frequency;
  }

  public Integer getPaymentsPerYearInWeeks() {
    return paymentsPerYearInWeeks;
  }
}
