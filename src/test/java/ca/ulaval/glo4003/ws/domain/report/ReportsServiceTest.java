package ca.ulaval.glo4003.ws.domain.report;

import ca.ulaval.glo4003.ws.domain.report.sales.SalesReport;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportFactory;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportIssuer;
import ca.ulaval.glo4003.ws.domain.report.sales.exception.NoSalesInTimeFrameException;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportsServiceTest {
  private static final LocalDate A_DATE = LocalDate.of(1, 1, 1);
  private static Collection<User> administrators;

  @Mock private LocalDateProvider localDateProvider;
  @Mock private SalesReportFactory salesReportFactory;
  @Mock private UserFinder userFinder;
  @Mock private SalesReportIssuer salesReportIssuer;
  @Mock private SalesReport salesReport;
  @Mock private User aUser;
  @Mock private User anotherUser;

  private ReportsService reportsService;

  @BeforeEach
  public void setUp() {
    administrators = List.of(aUser, anotherUser);

    reportsService =
        new ReportsService(localDateProvider, salesReportFactory, userFinder, salesReportIssuer);
  }

  @Test
  public void
      givenLocalDateAndCreatedSalesReport_whenAdvance_thenIssueSalesReportForAdministrators() {
    // given
    given(localDateProvider.today()).willReturn(A_DATE);
    given(salesReportFactory.create(A_DATE)).willReturn(salesReport);
    given(userFinder.findUsersWithRole(Role.ADMINISTRATOR)).willReturn(administrators);

    // when
    reportsService.advance();

    // then
    verify(salesReportIssuer).issueReport(aUser, salesReport);
    verify(salesReportIssuer).issueReport(anotherUser, salesReport);
  }

  @Test
  public void givenNoSalesInTimeFrame_whenAdvance_thenIssueEmptyReportForAdministrators() {
    // given
    given(localDateProvider.today()).willReturn(A_DATE);
    given(userFinder.findUsersWithRole(Role.ADMINISTRATOR)).willReturn(administrators);
    given(salesReportFactory.create(A_DATE)).willThrow(new NoSalesInTimeFrameException());

    // when
    reportsService.advance();

    // then
    verify(salesReportIssuer).issueEmptyReport(aUser, A_DATE);
    verify(salesReportIssuer).issueEmptyReport(anotherUser, A_DATE);
  }
}
