package ca.ulaval.glo4003.ws.domain.report.sales;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;

import java.time.LocalDate;

public class SalesReport {
  private final LocalDate reportDate;
  private final int numberOfSales;
  private final Price averagePrice;
  private final String mostSoldModel;
  private final String mostSoldBatteryType;

  public SalesReport(
      LocalDate reportDate,
      int numberOfSales,
      Price averagePrice,
      String mostSoldModel,
      String mostSoldBatteryType) {
    this.reportDate = reportDate;
    this.numberOfSales = numberOfSales;
    this.averagePrice = averagePrice;
    this.mostSoldModel = mostSoldModel;
    this.mostSoldBatteryType = mostSoldBatteryType;
  }

  public LocalDate getReportDate() {
    return reportDate;
  }

  public int getNumberOfSales() {
    return numberOfSales;
  }

  public Price getAveragePrice() {
    return averagePrice;
  }

  public String getMostSoldModel() {
    return mostSoldModel;
  }

  public String getMostSoldBatteryType() {
    return mostSoldBatteryType;
  }
}
