package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.user.User;

// TODO something else than Issuer ?
public interface NotificationIssuer {

  // TODO will change
  void issueDelayNotification(User user);
}
