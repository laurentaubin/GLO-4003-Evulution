package ca.ulaval.glo4003.ws.api.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.domain.transaction.BankAccountFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentRequestAssemblerTest {
  private static final String A_FREQUENCY = "monthly";
  private static final int A_BANK_NUMBER = 100;
  private static final int AN_ACCOUNT_NUMBER = 9999999;

  private PaymentRequestAssembler paymentRequestAssembler;
  private BankAccountFactory bankAccountFactory;

  @BeforeEach
  void setUp() {
    bankAccountFactory = new BankAccountFactory();
    paymentRequestAssembler = new PaymentRequestAssembler(bankAccountFactory);
  }

  @Test
  void givenPaymentRequest_whenCreate_thenReturnPaymentWithSameFields() {
    // given
    var paymentRequest = new PaymentRequest();
    paymentRequest.setBankNumber(A_BANK_NUMBER);
    paymentRequest.setAccountNumber(AN_ACCOUNT_NUMBER);
    paymentRequest.setFrequency(A_FREQUENCY);

    // when
    var actual = paymentRequestAssembler.create(paymentRequest);

    // then
    assertThat(actual.getBankAccount().getBankNumber()).isEqualTo(A_BANK_NUMBER);
    assertThat(actual.getBankAccount().getAccountNumber()).isEqualTo(AN_ACCOUNT_NUMBER);
    assertThat(actual.getFrequency().getFrequency()).matches(A_FREQUENCY);
  }
}
