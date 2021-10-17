package ca.ulaval.glo4003.ws.domain.notification;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    when(order.getId()).thenReturn(ORDER_ID);
  }

  @Test
  void whenSendDelayNotification_thenShouldFindUserFromRepository() {
    // when
    notificationService.listenVehicleAssemblyDelay(order);

    // then
    verify(userRepository).findUserByTransactionId(AN_ID);
  }

  @Test
  void
      givenTransactionIdAndFoundUser_whenSendDelayNotification_thenShouldIssueNotificationToUser() {
    // given
    when(userRepository.findUserByTransactionId(AN_ID)).thenReturn(user);

    // when
    notificationService.listenVehicleAssemblyDelay(order);

    // then
    verify(notificationIssuer).issueDelayNotification(user);
  }
}
