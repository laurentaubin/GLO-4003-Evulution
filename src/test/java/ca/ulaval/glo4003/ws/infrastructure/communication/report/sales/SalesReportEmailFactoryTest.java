package ca.ulaval.glo4003.ws.infrastructure.communication.report.sales;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.fixture.SalesReportFixture;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.Email;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailContent;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SalesReportEmailFactoryTest {
  private static final SalesReportFixture salesReportFixture = new SalesReportFixture();
  private static final String A_SENDER_EMAIL = "sender@email.com";
  private static final String A_RECIPIENT_EMAIL = "recipient@email.com";
  private static final String A_NAME = "Jogn";
  private static final LocalDate A_DATE = LocalDate.of(1, 1, 1);
  private static SalesReport salesReport;

  @Mock private EmailServer emailServer;
  @Mock private EmailContent regularEmailContent;
  @Mock private EmailContent emptyEmailContent;
  @Mock private User recipientUser;

  private SalesReportEmailFactory salesReportEmailFactory;

  @BeforeEach
  public void setUp() {
    salesReport = salesReportFixture.givenCompleteSalesReport();

    Map<ReportType, EmailContent> reportTemplates = new HashMap<>();
    reportTemplates.put(ReportType.REGULAR, regularEmailContent);
    reportTemplates.put(ReportType.EMPTY, emptyEmailContent);

    salesReportEmailFactory = new SalesReportEmailFactory(emailServer, reportTemplates);
  }

  @Test
  public void whenCreateSalesReportEmail_thenEmailHasRightSenderAndRecipientAddresses() {
    // given
    given(recipientUser.getEmail()).willReturn(A_RECIPIENT_EMAIL);

    // when
    Email actualEmail =
        salesReportEmailFactory.createSalesReportEmail(A_SENDER_EMAIL, recipientUser, salesReport);

    // then
    assertThat(actualEmail.getRecipientAddress()).matches(A_RECIPIENT_EMAIL);
    assertThat(actualEmail.getSenderAddress()).matches(A_SENDER_EMAIL);
  }

  @Test
  public void whenCreateSalesReportEmail_thenFormatSubjectWithDate() {
    // when
    salesReportEmailFactory.createSalesReportEmail(A_SENDER_EMAIL, recipientUser, salesReport);

    // then
    verify(regularEmailContent).formatSubject(salesReport.getReportDate());
  }

  @Test
  public void whenCreateSalesReportEmail_thenFormatBodyWithUserNameAndReportInformation() {
    // given
    given(recipientUser.getName()).willReturn(A_NAME);

    // when
    salesReportEmailFactory.createSalesReportEmail(A_SENDER_EMAIL, recipientUser, salesReport);

    // then
    verify(regularEmailContent)
        .formatBodyMessage(
            recipientUser.getName(),
            salesReport.getNumberOfSales(),
            salesReport.getAveragePrice().toDouble(),
            salesReport.getMostSoldModel(),
            salesReport.getMostSoldBatteryType());
  }

  @Test
  public void whenCreateEmptySalesReportEmail_thenEmailHasRightSenderAndRecipientAddresses() {
    // given
    given(recipientUser.getEmail()).willReturn(A_RECIPIENT_EMAIL);

    // when
    Email actualEmail =
        salesReportEmailFactory.createEmptySalesReport(A_SENDER_EMAIL, recipientUser, A_DATE);

    // then
    assertThat(actualEmail.getRecipientAddress()).matches(A_RECIPIENT_EMAIL);
    assertThat(actualEmail.getSenderAddress()).matches(A_SENDER_EMAIL);
  }

  @Test
  public void whenCreateEmptySalesReportEmail_thenFormatSubjectWithDate() {
    // when
    salesReportEmailFactory.createEmptySalesReport(A_SENDER_EMAIL, recipientUser, A_DATE);

    // then
    verify(emptyEmailContent).formatSubject(A_DATE);
  }

  @Test
  public void whenCreateEmptySalesReportEmail_thenFormatBodyWithUserNameAndReportInformation() {
    // given
    given(recipientUser.getName()).willReturn(A_NAME);

    // when
    salesReportEmailFactory.createEmptySalesReport(A_SENDER_EMAIL, recipientUser, A_DATE);

    // then
    verify(emptyEmailContent).formatBodyMessage(recipientUser.getName());
  }
}
