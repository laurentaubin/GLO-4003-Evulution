package ca.ulaval.glo4003.ws.domain.report.sales;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.domain.report.sales.exception.NoSalesInTimeFrameException;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogFinder;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalesReportFactoryTest {
  private static final LocalDate A_CREATION_DATE = LocalDate.of(1994, 6, 16);
  private static final Price A_PRICE = new Price(1000);
  private static final Price ANOTHER_PRICE = new Price(4000);
  private static final String A_MODEL = "Ford";
  private static final String ANOTHER_MODEL = "Tesla";
  private static final String A_BATTERY_TYPE = "Lithium";
  private static final String ANOTHER_BATTERY_TYPE = "Saline";

  @Mock TransactionLogFinder transactionLogFinder;
  
  private Collection<TransactionLogEntry> transactionLogs;
  private SalesReportFactory salesReportFactory;

  @BeforeEach
  public void setUp() {
    this.salesReportFactory = new SalesReportFactory(transactionLogFinder);
    transactionLogs = createTransactionLogs();
  }

  @Test
  public void givenNoTransactionLogsForDate_whenCreate_thenThrowNoSalesInTimeFrameException() {
    // given
    given(transactionLogFinder.findAllForDate(A_CREATION_DATE)).willReturn(Collections.emptyList());

    // when
    Executable creating = () -> salesReportFactory.create(A_CREATION_DATE);

    // then
    assertThrows(NoSalesInTimeFrameException.class, creating);
  }

  @Test
  public void givenTransactionLogsWithAveragePriceOf2000_whenCreate_thenReportHasCorrectAverage() {
    // given
    given(transactionLogFinder.findAllForDate(A_CREATION_DATE)).willReturn(transactionLogs);
    Price expectedAveragePrice = new Price(2000);

    // when
    SalesReport salesReport = salesReportFactory.create(A_CREATION_DATE);

    // then
    assertThat(salesReport.getAveragePrice()).isEqualTo(expectedAveragePrice);
  }

  @Test
  public void givenTransactionLogs_whenCreate_thenReportHasMostCommonModel() {
    // given
    given(transactionLogFinder.findAllForDate(A_CREATION_DATE)).willReturn(transactionLogs);

    // when
    SalesReport salesReport = salesReportFactory.create(A_CREATION_DATE);

    // then
    assertThat(salesReport.getMostSoldModel()).isEqualTo(A_MODEL);
  }

  @Test
  public void givenTransactionLogs_whenCreate_thenReportHasMostCommonBatteryType() {
    // given
    given(transactionLogFinder.findAllForDate(A_CREATION_DATE)).willReturn(transactionLogs);

    // when
    SalesReport salesReport = salesReportFactory.create(A_CREATION_DATE);

    // then
    assertThat(salesReport.getMostSoldBatteryType()).isEqualTo(A_BATTERY_TYPE);
  }

  @Test
  public void givenThreeTransactionLogs_whenCreate_thenReportHasThreeSales() {
    // given
    given(transactionLogFinder.findAllForDate(A_CREATION_DATE)).willReturn(transactionLogs);

    // when
    SalesReport salesReport = salesReportFactory.create(A_CREATION_DATE);

    // then
    assertThat(salesReport.getNumberOfSales()).isEqualTo(transactionLogs.size());
  }

  private static Collection<TransactionLogEntry> createTransactionLogs() {
    TransactionLogEntry aTransactionLog =
        new TransactionLogEntry(A_CREATION_DATE, A_PRICE, A_MODEL, A_BATTERY_TYPE);
    TransactionLogEntry anotherTransactionLog =
        new TransactionLogEntry(
            A_CREATION_DATE, ANOTHER_PRICE, ANOTHER_MODEL, ANOTHER_BATTERY_TYPE);

    return List.of(aTransactionLog, aTransactionLog, anotherTransactionLog);
  }
}
