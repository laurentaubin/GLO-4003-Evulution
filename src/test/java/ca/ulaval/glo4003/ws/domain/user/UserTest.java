package ca.ulaval.glo4003.ws.domain.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserTest {
  private static final Role A_ROLE = Role.ADMIN;

  @Mock private static TransactionId transactionId;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new UserBuilder().build();
  }

  @Test
  public void whenCreate_thenUserOnlyHasBaseRole() {
    assertThat(user.getRoles()).contains(Role.BASE);
    assertThat(user.getRoles()).hasSize(1);
  }

  @Test
  void givenDeliveryId_whenAddDelivery_thenDoesOwnDeliveryTrue() {
    // given
    DeliveryId deliveryId = new DeliveryId("1234");

    // when
    user.addDelivery(deliveryId);

    // then
    Assertions.assertTrue(user.doesOwnDelivery(deliveryId));
  }

  @Test
  void givenInvalidDeliveryId_whenDoesOwnDelivery_thenReturnFalse() {
    // given
    DeliveryId invalidDeliveryId = new DeliveryId("1234");

    // when
    boolean result = user.doesOwnDelivery(invalidDeliveryId);

    // then
    Assertions.assertFalse(result);
  }

  @Test
  void givenARole_whenAddRole_thenUserHasNewRole() {
    user.addRole(A_ROLE);

    assertThat(user.getRoles()).contains(A_ROLE);
  }

  @Test
  void givenUserPossessRequestedRole_whenIsAllowed_thenUserIsAllowed() {
    User userWithRequestedRoles = new UserBuilder().withRoles(List.of(Role.BASE)).build();
    List<Role> requestedRoles = List.of(Role.BASE, Role.ADMIN);

    boolean isAllowed = userWithRequestedRoles.isAllowed(requestedRoles);

    assertThat(isAllowed).isTrue();
  }

  @Test
  void givenUserWithoutRequestedRole_whenIsAllowed_thenUserIsNotAllowed() {
    User userWithRequestedRoles = new UserBuilder().withRoles(List.of(Role.BASE)).build();
    List<Role> requestedRoles = List.of(Role.ADMIN);

    boolean isAllowed = userWithRequestedRoles.isAllowed(requestedRoles);

    assertThat(isAllowed).isFalse();
  }

  @Test
  void givenTransactionAddedToUser_whenDoesOwnTransaction_thenReturnTrue() {
    // given
    user.addTransaction(transactionId);

    // when
    boolean doesUserOwnTransaction = user.doesOwnTransaction(transactionId);

    // then
    assertThat(doesUserOwnTransaction).isTrue();
  }

  @Test
  void givenTransactionNotAddedToUser_whenDoesOwnTransaction_thenReturnFalse() {

    // when
    boolean doesUserOwnTransaction = user.doesOwnTransaction(transactionId);

    // then
    assertThat(doesUserOwnTransaction).isFalse();
  }
}
