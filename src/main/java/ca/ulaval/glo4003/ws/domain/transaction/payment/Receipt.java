package ca.ulaval.glo4003.ws.domain.transaction.payment;

public class Receipt {
  private final Integer amountPerPeriod;
  private final Integer balance;
  private final Integer amountOfYearsToPayOver;

  public Receipt(Integer price, Frequency frequency, Integer amountOfYearsToPayOver) {
    this.amountOfYearsToPayOver = amountOfYearsToPayOver;
    this.balance = price;
    this.amountPerPeriod = computeAmountPerPeriod(price, frequency);
  }

  public Integer getPaymentsLeft() {
    return (balance - amountPerPeriod) / amountPerPeriod;
  }

  public Integer getAmountPerPeriod() {
    return amountPerPeriod;
  }

  private Integer computeAmountPerPeriod(Integer price, Frequency frequency) {
    Integer periods = computePeriods(frequency);
    return price / periods;
  }

  private Integer computePeriods(Frequency frequency) {
    return frequency.getPaymentsPerYearInWeeks() * amountOfYearsToPayOver;
  }
}
