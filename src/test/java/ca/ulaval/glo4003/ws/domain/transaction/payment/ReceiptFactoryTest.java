package ca.ulaval.glo4003.ws.domain.transaction.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ReceiptFactoryTest {
  private static final Integer A_VEHICLE_PRICE = 72000;
  private static final Frequency A_MONTHLY_FREQUENCY = Frequency.MONTHLY;
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;

  private ReceiptFactory receiptFactory;

  @BeforeEach
  public void setUp() {
    receiptFactory = new ReceiptFactory(AMOUNT_OF_YEARS_TO_PAY_OVER);
  }

  @Test
  public void whenCreate_thenCreateReceipt() {
    // when
    Receipt receipt = receiptFactory.create(A_VEHICLE_PRICE, A_MONTHLY_FREQUENCY);
    Integer expectedAmountPerPeriod = 1000;
    Integer expectedPaymentsLeft = 71;

    // then
    assertThat(receipt.getAmountPerPeriod()).isEqualTo(expectedAmountPerPeriod);
    assertThat(receipt.getPaymentsLeft()).isEqualTo(expectedPaymentsLeft);
  }
}
