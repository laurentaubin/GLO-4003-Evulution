package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import ca.ulaval.glo4003.ws.domain.warehouse.DelayType;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
  private static final OrderId AN_ORDER_ID = new OrderId("id");
  private static final TransactionId AN_ID = TransactionId.fromString("id");

  @Mock private Order order;
  @Mock private User user;
  @Mock private UserFinder userFinder;
  @Mock private NotificationIssuer notificationIssuer;

  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    notificationService = new NotificationService(notificationIssuer, userFinder);

    given(order.getId()).willReturn(AN_ORDER_ID);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenVehicleAssemblyDelay_thenShouldIssueVehicleAssemblyNotification() {
    // given
    given(userFinder.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenVehicleOrderDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.VEHICLE_ASSEMBLY);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenModelAssemblyDelay_thenShouldIssueModelAssemblyDelayNotification() {
    // given
    given(userFinder.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenModelOrderDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.MODEL_ASSEMBLY);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenBatteryAssemblyDelay_thenShouldIssueBatteryAssemblyDelayNotification() {
    // given
    given(userFinder.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenBatteryOrderDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.BATTERY_ASSEMBLY);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenProductionLineShutdown_thenShouldIssueProductionShutdownNotification() {
    // given
    given(userFinder.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenToOrderDelay(new ArrayList<>(List.of(order)));

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.PRODUCTION_SHUTDOWN);
  }
}
