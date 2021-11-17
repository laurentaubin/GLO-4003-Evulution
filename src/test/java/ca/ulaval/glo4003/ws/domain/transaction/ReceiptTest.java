package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ReceiptTest {
  private static final Integer A_PRICE = 72000;
  private static final Frequency A_MONTHLY_FREQUENCY = Frequency.MONTHLY;
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;

  @Test
  void givenAReceipt_whenGetAmountPerPeriod_thenReturnCorrectAmountPerPeriod() {
    // given
    Receipt receipt = givenAReceipt();
    Integer expectedAmountPerPeriod = 1000;

    // when
    Integer actualAmountPerPeriod = receipt.getAmountPerPeriod();

    // then
    assertThat(expectedAmountPerPeriod).isEqualTo(actualAmountPerPeriod);
  }

  @Test
  void givenAReceipt_whenPaymentsLeft_thenReturnPaymentsLeft() {
    // given
    Receipt receipt = givenAReceipt();
    Integer expectedPaymentsLeft = 71;

    // when
    Integer actualPaymentsLeft = receipt.getPaymentsLeft();

    // then
    assertThat(expectedPaymentsLeft).isEqualTo(actualPaymentsLeft);
  }

  private Receipt givenAReceipt() {
    return new Receipt(A_PRICE, A_MONTHLY_FREQUENCY, AMOUNT_OF_YEARS_TO_PAY_OVER);
  }
}
