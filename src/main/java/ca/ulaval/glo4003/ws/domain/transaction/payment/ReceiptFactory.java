package ca.ulaval.glo4003.ws.domain.transaction.payment;

public class ReceiptFactory {
  private final Integer amountOfYearsToPayOver;

  public ReceiptFactory(Integer amountOfYearsToPayOver) {
    this.amountOfYearsToPayOver = amountOfYearsToPayOver;
  }

  public Receipt create(Price vehiclePrice, Frequency paymentFrequency) {
    return new Receipt(vehiclePrice, paymentFrequency, amountOfYearsToPayOver);
  }
}
