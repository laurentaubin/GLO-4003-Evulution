package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.assembly.DelayType;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.user.User;

public interface NotificationIssuer {

  void issueDelayNotification(User user, Order order, DelayType delayType);
}
