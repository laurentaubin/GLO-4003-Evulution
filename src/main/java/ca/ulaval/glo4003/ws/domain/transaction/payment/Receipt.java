package ca.ulaval.glo4003.ws.domain.transaction.payment;

public class Receipt {
  private final Price amountPerPeriod;
  private final Price balance;
  private final Integer amountOfYearsToPayOver;

  public Receipt(Price price, Frequency frequency, Integer amountOfYearsToPayOver) {
    this.amountOfYearsToPayOver = amountOfYearsToPayOver;
    this.balance = price;
    this.amountPerPeriod = computeAmountPerPeriod(price, frequency);
  }

  public Integer getPaymentsLeft() {
    return (balance.subtract(amountPerPeriod)).divide(amountPerPeriod.toDouble()).toInt();
  }

  public Price getAmountPerPeriod() {
    return amountPerPeriod;
  }

  private Price computeAmountPerPeriod(Price price, Frequency frequency) {
    Integer periods = computePeriods(frequency);
    return price.divide(periods);
  }

  private Integer computePeriods(Frequency frequency) {
    return frequency.getPaymentsPerYearInWeeks() * amountOfYearsToPayOver;
  }
}
