package ca.ulaval.glo4003.ws.domain.report;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportFactory;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportIssuer;
import ca.ulaval.glo4003.ws.domain.report.sales.exception.NoSalesInTimeFrameException;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;

import java.time.LocalDate;
import java.util.Collection;

public class ReportsService {
  private final LocalDateProvider localDateProvider;
  private final SalesReportFactory salesReportFactory;
  private final UserFinder userFinder;
  private final SalesReportIssuer salesReportIssuer;

  public ReportsService(
      LocalDateProvider localDateProvider,
      SalesReportFactory salesReportFactory,
      UserFinder userFinder,
      SalesReportIssuer salesReportIssuer) {
    this.localDateProvider = localDateProvider;
    this.userFinder = userFinder;
    this.salesReportFactory = salesReportFactory;
    this.salesReportIssuer = salesReportIssuer;
  }

  public void advance() {
    issueSalesReport();
  }

  private void issueSalesReport() {
    LocalDate today = localDateProvider.today();
    Collection<User> administrators = userFinder.findUsersWithRole(Role.ADMINISTRATOR);

    try {
      sendRegularSaleReports(today, administrators);
    } catch (NoSalesInTimeFrameException ignored) {
      sendEmptySaleReports(today, administrators);
    }
  }

  private void sendRegularSaleReports(LocalDate today, Collection<User> administrators) {
    SalesReport weeklySalesReport = salesReportFactory.create(today);
    for (User user : administrators) {
      salesReportIssuer.issueReport(user, weeklySalesReport);
    }
  }

  private void sendEmptySaleReports(LocalDate today, Collection<User> administrators) {
    for (User user : administrators) {
      salesReportIssuer.issueEmptyReport(user, today);
    }
  }
}
