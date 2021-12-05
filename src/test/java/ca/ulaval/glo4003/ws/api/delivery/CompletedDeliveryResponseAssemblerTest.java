package ca.ulaval.glo4003.ws.api.delivery;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletedDeliveryResponseAssemblerTest {
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;
  private static final Price A_PRICE = new Price(new BigDecimal(1200));

  private CompletedDeliveryResponseAssembler completedDeliveryResponseAssembler;

  @BeforeEach
  void setUp() {
    completedDeliveryResponseAssembler = new CompletedDeliveryResponseAssembler();
  }

  @Test
  void givenReceipt_whenAssemble_thenReturnCorrectResponse() {
    // given
    int expectedPaymentTaken = 17;
    int expectedPaymentsLeft = 70;
    var receipt = new Receipt(A_PRICE, Frequency.MONTHLY, AMOUNT_OF_YEARS_TO_PAY_OVER);

    // when
    CompletedDeliveryResponse actual = completedDeliveryResponseAssembler.assemble(receipt);

    // then
    assertThat(actual.paymentsLeft).isEqualTo(expectedPaymentsLeft);
    assertThat(actual.paymentTaken).isEqualTo(expectedPaymentTaken);
  }
}
