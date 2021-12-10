package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.exception.NoTransactionLinkedToDeliveryException;
import ca.ulaval.glo4003.ws.fixture.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserTest {
  private static final Role A_ROLE = Role.PRODUCTION_MANAGER;
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("1234");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("abcd");

  @Mock private static TransactionId transactionId;
  @Mock private static DeliveryId deliveryId;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new UserBuilder().build();
  }

  @Test
  public void whenCreate_thenUserOnlyHasBaseRole() {
    assertThat(user.getRoles()).contains(Role.CUSTOMER);
    assertThat(user.getRoles()).hasSize(1);
  }

  @Test
  void givenATransactionIdAndADeliveryId_whenAddTransactionDelivery_thenDoesOwnDeliveryTrue() {
    // given
    TransactionId transactionId = new TransactionId("34");
    DeliveryId deliveryId = new DeliveryId("1234");

    // when
    user.addTransactionDelivery(transactionId, deliveryId);

    // then
    assertThat(user.ownDelivery(deliveryId)).isTrue();
  }

  @Test
  void givenInvalidDeliveryId_whenDoesOwnDelivery_thenReturnFalse() {
    // given
    DeliveryId invalidDeliveryId = new DeliveryId("1234");

    // when
    boolean result = user.ownDelivery(invalidDeliveryId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void givenARole_whenAddRole_thenUserHasNewRole() {
    // when
    user.addRole(A_ROLE);

    // then
    assertThat(user.getRoles()).contains(A_ROLE);
  }

  @Test
  void givenUserPossessRequestedRole_whenIsAllowed_thenUserIsAllowed() {
    // given
    User userWithRequestedRoles = new UserBuilder().withRoles(List.of(Role.CUSTOMER)).build();
    List<Role> requestedRoles = List.of(Role.CUSTOMER, Role.PRODUCTION_MANAGER);

    // when
    boolean isAllowed = userWithRequestedRoles.isAllowed(requestedRoles);

    // then
    assertThat(isAllowed).isTrue();
  }

  @Test
  void
      givenValidDeliveryId_whenGettingTransactionIdFromDeliveryId_thenReturnCorrespondingTransactionId() {
    // given
    User user = new UserBuilder().build();
    user.addTransactionDelivery(A_TRANSACTION_ID, A_DELIVERY_ID);

    // when
    TransactionId fetchedTransactionId = user.getTransactionIdFromDeliveryId(A_DELIVERY_ID);

    // then
    assertThat(A_TRANSACTION_ID).isEqualTo(fetchedTransactionId);
  }

  @Test
  void
      givenAnInvalidDeliveryId_whenGettingTransactionIdFromDeliveryId_thenThrowsNoTransactionLinkedToDeliveryException() {
    // given
    User user = new UserBuilder().build();
    user.addTransactionDelivery(A_TRANSACTION_ID, A_DELIVERY_ID);
    DeliveryId aDifferentDeliveryId = new DeliveryId("a different id");

    // when
    Executable gettingTransactionIdFromDeliveryId =
        () -> user.getTransactionIdFromDeliveryId(aDifferentDeliveryId);

    // then
    assertThrows(NoTransactionLinkedToDeliveryException.class, gettingTransactionIdFromDeliveryId);
  }

  @Test
  void givenUserWithoutRequestedRole_whenIsAllowed_thenUserIsNotAllowed() {
    // given
    User userWithRequestedRoles = new UserBuilder().withRoles(List.of(Role.PRODUCTION_MANAGER)).build();
    List<Role> requestedRoles = List.of(Role.CUSTOMER);

    // when
    boolean isAllowed = userWithRequestedRoles.isAllowed(requestedRoles);

    // then
    assertThat(isAllowed).isFalse();
  }

  @Test
  void givenTransactionAddedToUser_whenOwnsTransaction_thenReturnTrue() {
    // given
    user.addTransactionDelivery(transactionId, deliveryId);

    // when
    boolean doesUserOwnTransaction = user.ownsTransaction(transactionId);

    // then
    assertThat(doesUserOwnTransaction).isTrue();
  }

  @Test
  void givenTransactionNotAddedToUser_whenOwnsTransaction_thenReturnFalse() {
    // when
    boolean doesUserOwnTransaction = user.ownsTransaction(transactionId);

    // then
    assertThat(doesUserOwnTransaction).isFalse();
  }
}
