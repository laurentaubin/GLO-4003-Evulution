package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import java.time.LocalDate;

public class TransactionLogFixture {
  private static final LocalDate A_DATE = LocalDate.of(1, 1, 1);
  private static final Price A_PRICE = new Price(123);
  private static final String A_MODEL = "model";
  private static final String A_BATTERY_TYPE = "battery";

  public TransactionLogEntry givenATransactionLog() {
    return new TransactionLogEntry(A_DATE, A_PRICE, A_MODEL, A_BATTERY_TYPE);
  }
}
