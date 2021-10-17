package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;

public class NotificationService implements VehicleAssemblyDelayObserver {
  private final NotificationIssuer notificationIssuer;
  private final UserRepository userRepository;

  public NotificationService(NotificationIssuer notificationIssuer, UserRepository userRepository) {
    this.notificationIssuer = notificationIssuer;
    this.userRepository = userRepository;
  }

  public void sendDelayNotification(Order order) {
    TransactionId transactionId = TransactionId.fromString(order.getId().toString());
    User user = userRepository.findUserByTransactionId(transactionId);
    notificationIssuer.issueDelayNotification(user);
  }

  @Override
  public void listenVehicleAssemblyDelay(Order order) {
    sendDelayNotification(order);
  }
}
