package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.warehouse.DelayType;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.fixture.UserBuilder;
import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.exception.NotificationContentNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailNotificationIssuerTest {
  private static final String SENDER_EMAIL_ADDRESS = "SIKETHATSTHEWRONGNUMBER@outlook.fr";
  private static final DelayType A_DELAY_TYPE = DelayType.VEHICLE_ASSEMBLY;

  @Mock private Email email;
  @Mock private Order order;
  @Mock private NotificationEmailFactory notificationEmailFactory;

  private EmailNotificationIssuer emailNotificationIssuer;

  @BeforeEach
  public void setUp() {
    emailNotificationIssuer =
        new EmailNotificationIssuer(SENDER_EMAIL_ADDRESS, notificationEmailFactory);
  }

  @Test
  public void
      givenModelAssemblyDelayType_whenIssueDelayNotification_thenCreateEmailWithModelAssemblyDelayNotificationType() {
    // given
    User user = new UserBuilder().build();
    given(
            notificationEmailFactory.createDelayNotificationEmail(
                NotificationType.MODEL_ASSEMBLY_DELAY, order, SENDER_EMAIL_ADDRESS, user))
        .willReturn(email);

    // when
    emailNotificationIssuer.issueDelayNotification(user, order, DelayType.MODEL_ASSEMBLY);

    // then
    verify(email).send();
  }

  @Test
  public void
      givenBatteryAssemblyDelayType_whenIssueDelayNotification_thenCreateEmailWithBatteryAssemblyDelayNotificationType() {
    // given
    User user = new UserBuilder().build();
    given(
            notificationEmailFactory.createDelayNotificationEmail(
                NotificationType.BATTERY_ASSEMBLY_DELAY, order, SENDER_EMAIL_ADDRESS, user))
        .willReturn(email);

    // when
    emailNotificationIssuer.issueDelayNotification(user, order, DelayType.BATTERY_ASSEMBLY);

    // then
    verify(email).send();
  }

  @Test
  public void
      givenVehicleAssemblyDelayType_whenIssueDelayNotification_thenCreateEmailWithVehicleAssemblyDelayNotificationType() {
    // given
    User user = new UserBuilder().build();
    given(
            notificationEmailFactory.createDelayNotificationEmail(
                NotificationType.VEHICLE_ASSEMBLY_DELAY, order, SENDER_EMAIL_ADDRESS, user))
        .willReturn(email);

    // when
    emailNotificationIssuer.issueDelayNotification(user, order, DelayType.VEHICLE_ASSEMBLY);

    // then
    verify(email).send();
  }

  @Test
  public void givenNotificationContentNotRegistered_whenIssueDelayNotification_thenDoNotThrow() {
    // given
    User user = new UserBuilder().build();
    given(notificationEmailFactory.createDelayNotificationEmail(any(), any(), any(), any()))
        .willThrow(NotificationContentNotRegisteredException.class);

    // when
    Executable issuingNotification =
        () -> emailNotificationIssuer.issueDelayNotification(user, order, A_DELAY_TYPE);

    // then
    assertDoesNotThrow(issuingNotification);
  }
}
