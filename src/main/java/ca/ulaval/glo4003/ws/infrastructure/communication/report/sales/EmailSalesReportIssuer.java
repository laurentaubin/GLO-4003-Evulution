package ca.ulaval.glo4003.ws.infrastructure.communication.report.sales;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportIssuer;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.Email;

import java.time.LocalDate;

public class EmailSalesReportIssuer implements SalesReportIssuer {
  private final String senderEmail;
  private final SalesReportEmailFactory salesReportEmailFactory;

  public EmailSalesReportIssuer(
      String senderEmail, SalesReportEmailFactory salesReportEmailFactory) {
    this.senderEmail = senderEmail;
    this.salesReportEmailFactory = salesReportEmailFactory;
  }

  @Override
  public void issueReport(User user, SalesReport salesReport) {
    Email email = salesReportEmailFactory.createSalesReportEmail(senderEmail, user, salesReport);
    email.send();
  }

  @Override
  public void issueEmptyReport(User user, LocalDate reportDate) {
    Email email = salesReportEmailFactory.createEmptySalesReport(senderEmail, user, reportDate);
    email.send();
  }
}
