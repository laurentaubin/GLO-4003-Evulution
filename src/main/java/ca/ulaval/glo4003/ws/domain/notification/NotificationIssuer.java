package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.warehouse.DelayType;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;

public interface NotificationIssuer {

  void issueDelayNotification(User user, Order order, DelayType delayType);
}
