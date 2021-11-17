package ca.ulaval.glo4003.ws.domain.transaction.payment;

public class Payment {
  private final BankAccount bankAccount;
  private final Frequency frequency;

  public Payment(BankAccount bankAccount, Frequency frequency) {
    this.bankAccount = bankAccount;
    this.frequency = frequency;
  }

  public BankAccount getBankAccount() {
    return bankAccount;
  }

  public Frequency getFrequency() {
    return frequency;
  }
}
