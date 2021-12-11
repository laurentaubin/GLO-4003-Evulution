package ca.ulaval.glo4003.ws.infrastructure.communication.report.sales;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSalesReportIssuerTest {
  private static final String A_SENDER_EMAIL = "email@emial.com";
  private static final LocalDate A_DATE = LocalDate.of(3, 3, 3);

  @Mock private SalesReportEmailFactory salesReportEmailFactory;
  @Mock private User user;
  @Mock private SalesReport salesReport;
  @Mock private Email email;

  private EmailSalesReportIssuer emailSalesReportIssuer;

  @BeforeEach
  public void setUp() {
    emailSalesReportIssuer = new EmailSalesReportIssuer(A_SENDER_EMAIL, salesReportEmailFactory);
  }

  @Test
  public void givenEmailCreated_whenIssueReport_thenSendEmail() {
    // given
    given(salesReportEmailFactory.createSalesReportEmail(A_SENDER_EMAIL, user, salesReport))
        .willReturn(email);

    // when
    emailSalesReportIssuer.issueReport(user, salesReport);

    // then
    verify(email).send();
  }

  @Test
  public void givenEmailCreated_whenIssueEmptyReport_thenSendEmail() {

    // given
    given(salesReportEmailFactory.createEmptySalesReport(A_SENDER_EMAIL, user, A_DATE))
        .willReturn(email);

    // when
    emailSalesReportIssuer.issueEmptyReport(user, A_DATE);

    // then
    verify(email).send();
  }
}
