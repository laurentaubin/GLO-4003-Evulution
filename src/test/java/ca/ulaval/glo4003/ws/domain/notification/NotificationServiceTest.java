package ca.ulaval.glo4003.ws.domain.notification;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.DelayType;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
  private static final OrderId ORDER_ID = new OrderId("id");
  private static final TransactionId AN_ID = TransactionId.fromString("id");

  @Mock private Order order;
  @Mock private User user;
  @Mock private UserRepository userRepository;
  @Mock private NotificationIssuer notificationIssuer;

  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    notificationService = new NotificationService(notificationIssuer, userRepository);

    given(order.getId()).willReturn(ORDER_ID);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenVehicleAssemblyDelay_thenShouldIssueVehicleAssemblyNotification() {
    // given
    given(userRepository.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenVehicleAssemblyDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.VEHICLE_ASSEMBLY);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenModelAssemblyDelay_thenShouldIssueModelAssemblyDelayNotification() {
    // given
    given(userRepository.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenModelAssemblyDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.MODEL_ASSEMBLY);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenListenBatteryAssemblyDelay_thenShouldIssueBatteryAssemblyDelayNotification() {
    // given
    given(userRepository.findUserByTransactionId(AN_ID)).willReturn(user);

    // when
    notificationService.listenBatteryAssemblyDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user, order, DelayType.BATTERY_ASSEMBLY);
  }
}
