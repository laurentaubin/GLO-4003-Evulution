package ca.ulaval.glo4003.ws.infrastructure.communication.report.sales;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.Email;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailContent;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailServer;

import java.time.LocalDate;
import java.util.Map;

public class SalesReportEmailFactory {
  private final EmailServer emailServer;
  private final Map<ReportType, EmailContent> reportTemplates;

  public SalesReportEmailFactory(
      EmailServer emailServer, Map<ReportType, EmailContent> reportTemplates) {
    this.emailServer = emailServer;
    this.reportTemplates = reportTemplates;
  }

  public Email createSalesReportEmail(String senderEmail, User user, SalesReport salesReport) {
    EmailContent reportTemplate = reportTemplates.get(ReportType.REGULAR);
    reportTemplate.formatSubject(salesReport.getReportDate());
    reportTemplate.formatBodyMessage(
        user.getName(),
        salesReport.getNumberOfSales(),
        salesReport.getAveragePrice().toDouble(),
        salesReport.getMostSoldModel(),
        salesReport.getMostSoldBatteryType());

    return new Email(emailServer, senderEmail, user.getEmail(), reportTemplate);
  }

  public Email createEmptySalesReport(String senderEmail, User user, LocalDate reportWeek) {
    EmailContent reportTemplate = reportTemplates.get(ReportType.EMPTY);
    reportTemplate.formatSubject(reportWeek);
    reportTemplate.formatBodyMessage(user.getName());

    return new Email(emailServer, senderEmail, user.getEmail(), reportTemplate);
  }
}
