package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class CompletedDeliveryResponseAssemblerTest {
  private static final Integer AN_AMOUNT_OF_YEARS_TO_PAY_OVER = 6;
  private static final Price A_PRICE = new Price(new BigDecimal(1200));
  private static final int A_NUMBER_OF_PAYMENT_TAKEN = 17;
  private static final int A_NUMBER_OF_PAYMENTS_LEFT = 70;

  private CompletedDeliveryDtoAssembler completedDeliveryAssembler;

  @BeforeEach
  void setUp() {
    completedDeliveryAssembler = new CompletedDeliveryDtoAssembler();
  }

  @Test
  void givenReceipt_whenAssemble_thenReturnCorrectResponse() {
    // given
    Receipt receipt = new Receipt(A_PRICE, Frequency.MONTHLY, AN_AMOUNT_OF_YEARS_TO_PAY_OVER);

    // when
    CompletedDeliveryDto actual = completedDeliveryAssembler.assemble(receipt);

    // then
    assertThat(actual.getPaymentsLeft()).isEqualTo(A_NUMBER_OF_PAYMENTS_LEFT);
    assertThat(actual.getPaymentTaken()).isEqualTo(A_NUMBER_OF_PAYMENT_TAKEN);
  }
}
