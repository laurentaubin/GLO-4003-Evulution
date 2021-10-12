package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.exception.NotificationContentNotRegisteredException;
import java.util.Map;
import java.util.Optional;

public class NotificationEmailFactory {

  private final EmailServer emailServer;
  private final Map<NotificationType, EmailContentDto> emailContents;

  public NotificationEmailFactory(
      EmailServer emailServer, Map<NotificationType, EmailContentDto> emailContents) {
    this.emailServer = emailServer;
    this.emailContents = emailContents;
  }

  public Email createAssemblyLineDelayNotificationEmail(String sender, String recipient) {
    EmailContentDto emailContent =
        Optional.ofNullable(emailContents.get(NotificationType.ASSEMBLY_LINE_DELAY))
            .orElseThrow(
                () ->
                    new NotificationContentNotRegisteredException(
                        NotificationType.ASSEMBLY_LINE_DELAY));
    return new Email(emailServer, sender, recipient, emailContent);
  }
}
