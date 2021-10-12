package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import ca.ulaval.glo4003.ws.domain.notification.NotificationIssuer;
import ca.ulaval.glo4003.ws.domain.user.User;
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
  public void issueDelayNotification(User user) {
    Email email;
    try {
      email =
          notificationEmailFactory.createAssemblyLineDelayNotificationEmail(
              notificationEmailAddress, user.getEmail());
    } catch (NotificationContentNotRegisteredException exception) {
      LOGGER.error(exception.getMessage(), exception);
      return;
    }
    email.send();
  }
}
