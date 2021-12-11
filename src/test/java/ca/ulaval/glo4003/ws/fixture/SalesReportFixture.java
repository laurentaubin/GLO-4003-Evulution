package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesReportFixture {
  private static final LocalDate A_DATE = LocalDate.of(1, 11, 11);
  private static final int A_NUMBER_OF_SALES = 12;
  private static final Price AN_AVERAGE_PRICE = new Price(BigDecimal.TEN);
  private static final String MOST_SOLD_MODEL = "Model";
  private static final String MOST_SOLD_BATTERY = "Batter";

  public SalesReport givenCompleteSalesReport() {
    return new SalesReport(
        A_DATE, A_NUMBER_OF_SALES, AN_AVERAGE_PRICE, MOST_SOLD_MODEL, MOST_SOLD_BATTERY);
  }
}
