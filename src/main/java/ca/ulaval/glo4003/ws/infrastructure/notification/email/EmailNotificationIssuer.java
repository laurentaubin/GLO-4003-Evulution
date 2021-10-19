package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import ca.ulaval.glo4003.ws.domain.assembly.DelayType;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.NotificationIssuer;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.exception.NotificationContentNotRegisteredException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailNotificationIssuer implements NotificationIssuer {
  private static final Logger LOGGER = LogManager.getLogger();

  private final String notificationEmailAddress;
  private final NotificationEmailFactory notificationEmailFactory;

  public EmailNotificationIssuer(
      String notificationEmailAddress, NotificationEmailFactory notificationEmailFactory) {
    this.notificationEmailAddress = notificationEmailAddress;
    this.notificationEmailFactory = notificationEmailFactory;
  }

  @Override
  public void issueDelayNotification(User user, Order order, DelayType delayType) {
    NotificationType notificationType = convertDelayTypeToNotificationType(delayType);
    Email email;
    try {
      email =
          notificationEmailFactory.createDelayNotificationEmail(
              notificationType, order, notificationEmailAddress, user);
    } catch (NotificationContentNotRegisteredException exception) {
      LOGGER.error(exception.getMessage(), exception);
      return;
    }

    email.send();
  }

  private NotificationType convertDelayTypeToNotificationType(DelayType delayType) {
    switch (delayType) {
      case MODEL_ASSEMBLY:
        return NotificationType.MODEL_ASSEMBLY_DELAY;
      case BATTERY_ASSEMBLY:
        return NotificationType.BATTERY_ASSEMBLY_DELAY;
      case VEHICLE_ASSEMBLY:
        return NotificationType.VEHICLE_ASSEMBLY_DELAY;
    }

    return null;
  }
}
