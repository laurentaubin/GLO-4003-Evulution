package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccount;
import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccountFactory;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.service.transaction.dto.PaymentRequest;

public class PaymentRequestAssembler {

  private final BankAccountFactory bankAccountFactory;

  public PaymentRequestAssembler(BankAccountFactory bankAccountFactory) {
    this.bankAccountFactory = bankAccountFactory;
  }

  public Payment create(PaymentRequest paymentRequest) {
    BankAccount bankAccount =
        bankAccountFactory.create(
            paymentRequest.getBankNumber(), paymentRequest.getAccountNumber());
    return new Payment(bankAccount, Frequency.fromString(paymentRequest.getFrequency()));
  }
}
