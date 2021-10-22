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
  private static final String A_VALID_FREQUENCY = "monthly";
  private static final String A_VALID_BANK_NUMBER = "003";
  private static final String A_VALID_ACCOUNT_NUMBER = "0099999";

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
    paymentRequest.setBankNumber(A_VALID_BANK_NUMBER);
    paymentRequest.setAccountNumber(A_VALID_ACCOUNT_NUMBER);
    paymentRequest.setFrequency(A_VALID_FREQUENCY);

    // when
    var actual = paymentRequestAssembler.create(paymentRequest);

    // then
    assertThat(actual.getBankAccount().getBankNumber()).isEqualTo(A_VALID_BANK_NUMBER);
    assertThat(actual.getBankAccount().getAccountNumber()).isEqualTo(A_VALID_ACCOUNT_NUMBER);
    assertThat(actual.getFrequency().getFrequency()).matches(A_VALID_FREQUENCY);
  }
}
