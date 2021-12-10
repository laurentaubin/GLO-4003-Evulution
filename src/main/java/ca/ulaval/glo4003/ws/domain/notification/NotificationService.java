package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import ca.ulaval.glo4003.ws.domain.warehouse.DelayType;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService
    implements ModelOrderDelayObserver,
        BatteryOrderDelayObserver,
        VehicleOrderDelayObserver,
        OrderDelayObserver {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private final NotificationIssuer notificationIssuer;
  private final UserFinder userFinder;

  public NotificationService() {
    this(
        serviceLocator.resolve(NotificationIssuer.class), serviceLocator.resolve(UserFinder.class));
  }

  public NotificationService(NotificationIssuer notificationIssuer, UserFinder userFinder) {
    this.notificationIssuer = notificationIssuer;
    this.userFinder = userFinder;
  }

  @Override
  public void listenVehicleOrderDelay(Order order) {
    User user = findOrderOwner(order);
    notificationIssuer.issueDelayNotification(user, order, DelayType.VEHICLE_ASSEMBLY);
  }

  @Override
  public void listenModelOrderDelay(Order order) {
    User user = findOrderOwner(order);
    notificationIssuer.issueDelayNotification(user, order, DelayType.MODEL_ASSEMBLY);
  }

  @Override
  public void listenBatteryOrderDelay(Order order) {
    User user = findOrderOwner(order);
    notificationIssuer.issueDelayNotification(user, order, DelayType.BATTERY_ASSEMBLY);
  }

  @Override
  public void listenToOrderDelay(List<Order> orders) {
    Map<Order, User> orderUserMap = findOrderOwners(orders);
    orderUserMap.forEach(
        (order, user) ->
            notificationIssuer.issueDelayNotification(user, order, DelayType.PRODUCTION_SHUTDOWN));
  }

  private User findOrderOwner(Order order) {
    TransactionId transactionId = TransactionId.fromString(order.getId().toString());
    return userFinder.findUserByTransactionId(transactionId);
  }

  private Map<Order, User> findOrderOwners(List<Order> orders) {
    Map<Order, User> orderUserMap = new HashMap<>();
    orders.forEach(order -> orderUserMap.put(order, findOrderOwner(order)));
    return orderUserMap;
  }
}
