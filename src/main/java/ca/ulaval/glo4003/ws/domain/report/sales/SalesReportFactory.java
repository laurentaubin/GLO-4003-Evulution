package ca.ulaval.glo4003.ws.domain.report.sales;

import ca.ulaval.glo4003.ws.domain.report.sales.exception.NoSalesInTimeFrameException;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogFinder;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SalesReportFactory {
  private final TransactionLogFinder transactionLogFinder;

  public SalesReportFactory(TransactionLogFinder transactionLogFinder) {
    this.transactionLogFinder = transactionLogFinder;
  }

  public SalesReport create(LocalDate date) {
    Collection<TransactionLogEntry> saleLogs = transactionLogFinder.findAllForDate(date);

    if (saleLogs.isEmpty()) {
      throw new NoSalesInTimeFrameException();
    }

    return new SalesReport(
        LocalDate.now(),
        saleLogs.size(),
        calculateAveragePrice(saleLogs),
        calculateMostSoldModel(saleLogs),
        calculateMostSoldBatteryType(saleLogs));
  }

  private Price calculateAveragePrice(Collection<TransactionLogEntry> saleLogs) {
    Price totalPrice = new Price(BigDecimal.ZERO);
    for (TransactionLogEntry log : saleLogs) {
      totalPrice = totalPrice.add(log.getTotalPrice());
    }

    return totalPrice.divide(saleLogs.size());
  }

  private String calculateMostSoldModel(Collection<TransactionLogEntry> saleLogs) {
    Map<String, Long> logModelCount =
        saleLogs.stream()
            .collect(
                Collectors.groupingBy(
                    TransactionLogEntry::getVehicleModel, TreeMap::new, Collectors.counting()));
    return Collections.max(logModelCount.entrySet(), Map.Entry.comparingByValue()).getKey();
  }

  private String calculateMostSoldBatteryType(Collection<TransactionLogEntry> saleLogs) {
    Map<String, Long> logBatteryType =
        saleLogs.stream()
            .collect(
                Collectors.groupingBy(
                    TransactionLogEntry::getBatteryType, TreeMap::new, Collectors.counting()));
    return Collections.max(logBatteryType.entrySet(), Map.Entry.comparingByValue()).getKey();
  }
}
