package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ReceiptTest {
  private static final Price A_PRICE = new Price(new BigDecimal(72000));
  private static final Frequency A_MONTHLY_FREQUENCY = Frequency.MONTHLY;
  private static final Integer AN_AMOUNT_OF_YEARS_TO_PAY_OVER = 6;

  @Test
  void givenAReceipt_whenGetAmountPerPeriod_thenReturnCorrectAmountPerPeriod() {
    // given
    Receipt receipt = givenAReceipt();
    Integer expectedAmountPerPeriod = 1000;

    // when
    Integer actualAmountPerPeriod = receipt.getAmountPerPeriod().toInt();

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
    return new Receipt(A_PRICE, A_MONTHLY_FREQUENCY, AN_AMOUNT_OF_YEARS_TO_PAY_OVER);
  }
}
