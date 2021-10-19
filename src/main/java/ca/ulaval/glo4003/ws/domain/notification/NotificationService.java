package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.assembly.DelayType;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;

public class NotificationService
    implements ModelAssemblyDelayObserver,
        BatteryAssemblyDelayObserver,
        VehicleAssemblyDelayObserver {
  private final NotificationIssuer notificationIssuer;
  private final UserRepository userRepository;

  public NotificationService(NotificationIssuer notificationIssuer, UserRepository userRepository) {
    this.notificationIssuer = notificationIssuer;
    this.userRepository = userRepository;
  }

  @Override
  public void listenVehicleAssemblyDelay(Order order) {
    User user = findOrderOwner(order);
    notificationIssuer.issueDelayNotification(user, order, DelayType.VEHICLE_ASSEMBLY);
  }

  @Override
  public void listenModelAssemblyDelay(Order order) {
    User user = findOrderOwner(order);
    notificationIssuer.issueDelayNotification(user, order, DelayType.MODEL_ASSEMBLY);
  }

  @Override
  public void listenBatteryAssemblyDelay(Order order) {
    User user = findOrderOwner(order);
    notificationIssuer.issueDelayNotification(user, order, DelayType.BATTERY_ASSEMBLY);
  }

  private User findOrderOwner(Order order) {
    TransactionId transactionId = TransactionId.fromString(order.getId().toString());
    return userRepository.findUserByTransactionId(transactionId);
  }
}
