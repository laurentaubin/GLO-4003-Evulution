package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.notification.exception.NotificationContentNotRegisteredException;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailNotificationIssuerTest {
  private static final String AN_EMAIL_ADDRESS = "benjaminjetaime@outlook.fr";
  private static final String ANOTHER_EMAIL_ADDRESS = "SIKETHATSTHEWRONGNUMBER@outlook.fr";

  @Mock private Email email;
  @Mock private NotificationEmailFactory notificationEmailFactory;

  private EmailNotificationIssuer emailNotificationIssuer;

  @BeforeEach
  public void setUp() {
    emailNotificationIssuer =
        new EmailNotificationIssuer(ANOTHER_EMAIL_ADDRESS, notificationEmailFactory);
  }

  @Test
  public void givenUser_whenIssueDelayNotification_thenCreateEmailWithUserInfo() {
    // given
    User user = new UserBuilder().withEmail(AN_EMAIL_ADDRESS).build();
    given(
            notificationEmailFactory.createAssemblyLineDelayNotificationEmail(
                ANOTHER_EMAIL_ADDRESS, AN_EMAIL_ADDRESS))
        .willReturn(email);

    // when
    emailNotificationIssuer.issueDelayNotification(user);

    // then
    verify(email).send();
  }

  @Test
  public void givenNotificationContentNotRegistered_whenIssueDelayNotification_thenDoNotThrow() {
    // given
    User user = new UserBuilder().build();
    given(notificationEmailFactory.createAssemblyLineDelayNotificationEmail(any(), any()))
        .willThrow(NotificationContentNotRegisteredException.class);

    // when
    Executable issuingNotification = () -> emailNotificationIssuer.issueDelayNotification(user);

    // then
    assertDoesNotThrow(issuingNotification);
  }
}
