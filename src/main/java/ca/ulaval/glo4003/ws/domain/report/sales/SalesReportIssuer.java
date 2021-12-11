package ca.ulaval.glo4003.ws.domain.report.sales;

import ca.ulaval.glo4003.ws.domain.user.User;

import java.time.LocalDate;

public interface SalesReportIssuer {
  void issueReport(User user, SalesReport salesReport);

  void issueEmptyReport(User user, LocalDate reportWeek);
}
