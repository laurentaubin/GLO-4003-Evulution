package ca.ulaval.glo4003.ws.service.delivery;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletedDeliveryResponseAssemblerTest {
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;
  private static final Price A_PRICE = new Price(new BigDecimal(1200));
  private static final int A_PAYMENT_TAKEN = 17;
  private static final int PAYMENTS_LEFT = 70;

  private CompletedDeliveryDtoAssembler completedDeliveryAssembler;

  @BeforeEach
  void setUp() {
    completedDeliveryAssembler = new CompletedDeliveryDtoAssembler();
  }

  @Test
  void givenReceipt_whenAssemble_thenReturnCorrectResponse() {
    // given
    Receipt receipt = new Receipt(A_PRICE, Frequency.MONTHLY, AMOUNT_OF_YEARS_TO_PAY_OVER);

    // when
    CompletedDeliveryDto actual = completedDeliveryAssembler.assemble(receipt);

    // then
    assertThat(actual.getPaymentsLeft()).isEqualTo(PAYMENTS_LEFT);
    assertThat(actual.getPaymentTaken()).isEqualTo(A_PAYMENT_TAKEN);
  }
}
