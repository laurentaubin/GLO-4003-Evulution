package ca.ulaval.glo4003.ws.infrastructure.notification.exception;

import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;

public class NotificationContentNotRegisteredException extends RuntimeException {
  public NotificationContentNotRegisteredException(NotificationType notificationType) {
    super(
        String.format(
            "Notification content not registered for notification type %s", notificationType));
  }
}
