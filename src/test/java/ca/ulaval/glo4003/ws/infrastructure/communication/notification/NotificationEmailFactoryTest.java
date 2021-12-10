package ca.ulaval.glo4003.ws.infrastructure.communication.notification;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.infrastructure.communication.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.Email;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailContent;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailServer;
import ca.ulaval.glo4003.ws.infrastructure.communication.exception.NotificationContentNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEmailFactoryTest {
  private static final String A_SENDER_EMAIL = "sender@email.com";
  private static final String A_RECIPIENT_EMAIL = "recipient@email.com";
  private static final String A_NAME = "Benjamin Girard <3";
  private static final OrderId AN_ORDER_ID = new OrderId("osadoasd");
  private static final LocalDate A_DELIVERY_DATE = LocalDate.of(1, 10, 10);
  private static final AssemblyTime A_DELAY = new AssemblyTime(1);
  private static final Map<NotificationType, EmailContent> emailContents = new HashMap<>();

  @Mock private EmailServer emailServer;
  @Mock private Order order;
  @Mock private User recipientUser;
  @Mock private EmailContent vehicleAssemblyDelayEmailContent;

  private NotificationEmailFactory notificationEmailFactory;

  @BeforeEach
  public void setUp() {
    emailContents.put(NotificationType.VEHICLE_ASSEMBLY_DELAY, vehicleAssemblyDelayEmailContent);

    notificationEmailFactory = new NotificationEmailFactory(emailServer, emailContents);
  }

  @Test
  public void
      givenVehicleAssemblyDelayNotification_whenCreateDelayNotificationEmail_thenReturnEmailWithRightSenderAndRecipient() {
    // given
    given(recipientUser.getEmail()).willReturn(A_RECIPIENT_EMAIL);
    given(order.getAssemblyDelay()).willReturn(A_DELAY);

    // when
    Email actualEmail =
        notificationEmailFactory.createDelayNotificationEmail(
            NotificationType.VEHICLE_ASSEMBLY_DELAY, order, A_SENDER_EMAIL, recipientUser);

    // then
    assertThat(actualEmail.getRecipientAddress()).matches(A_RECIPIENT_EMAIL);
    assertThat(actualEmail.getSenderAddress()).matches(A_SENDER_EMAIL);
  }

  @Test
  public void
      givenVehicleAssemblyDelayNotification_whenCreateDelayNotificationEmail_thenEmailSubjectIsFormattedWithOrderId() {
    // given
    given(order.getId()).willReturn(AN_ORDER_ID);
    given(order.getAssemblyDelay()).willReturn(A_DELAY);

    // when
    notificationEmailFactory.createDelayNotificationEmail(
        NotificationType.VEHICLE_ASSEMBLY_DELAY, order, A_SENDER_EMAIL, recipientUser);

    // then
    verify(vehicleAssemblyDelayEmailContent).formatSubject(AN_ORDER_ID);
  }

  @Test
  public void
      givenVehicleAssemblyDelayNotification_whenCreateDelayNotificationEmail_thenEmailBodyIsFormattedWithUserNameDelayAndExpectedDeliveryDate() {
    // given
    given(recipientUser.getName()).willReturn(A_NAME);
    given(order.getAssemblyDelay()).willReturn(A_DELAY);
    given(order.computeDeliveryDate()).willReturn(A_DELIVERY_DATE);

    // when
    notificationEmailFactory.createDelayNotificationEmail(
        NotificationType.VEHICLE_ASSEMBLY_DELAY, order, A_SENDER_EMAIL, recipientUser);

    // then
    verify(vehicleAssemblyDelayEmailContent)
        .formatBodyMessage(A_NAME, A_DELAY.inWeeks(), A_DELIVERY_DATE);
  }

  @Test
  public void
      givenEmailContentDtoNotPresent_whenCreateDelayNotificationEmail_thenThrowNotificationContentNotRegisteredException() {
    // given
    emailContents.clear();

    // when
    Executable creatingNotificationEmail =
        () ->
            notificationEmailFactory.createDelayNotificationEmail(
                NotificationType.VEHICLE_ASSEMBLY_DELAY, order, A_SENDER_EMAIL, recipientUser);

    // then
    assertThrows(NotificationContentNotRegisteredException.class, creatingNotificationEmail);
  }
}
