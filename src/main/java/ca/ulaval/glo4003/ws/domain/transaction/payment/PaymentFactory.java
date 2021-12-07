package ca.ulaval.glo4003.ws.domain.transaction.payment;

public class PaymentFactory {
  private final BankAccountFactory bankAccountFactory;

  public PaymentFactory(BankAccountFactory bankAccountFactory) {
    this.bankAccountFactory = bankAccountFactory;
  }

  public Payment create(String bankNumber, String accountNumber, String frequency) {
    BankAccount bankAccount = bankAccountFactory.create(bankNumber, accountNumber);
    return new Payment(bankAccount, Frequency.fromString(frequency));
  }
}
