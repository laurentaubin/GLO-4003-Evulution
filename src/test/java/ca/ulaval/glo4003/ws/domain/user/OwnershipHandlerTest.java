package ca.ulaval.glo4003.ws.domain.user;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OwnershipHandlerTest {
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("tx id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("delivery id");
  private static final String AN_EMAIL = "mail@mail.ca";
  private static final Session A_SESSION = new Session(new SessionToken("t0k3nV41u3"), AN_EMAIL);

  @Mock private UserRepository userRepository;
  @Mock private User user;
  @Mock private TransactionId transactionId;
  @Mock private DeliveryId deliveryId;

  private OwnershipHandler ownershipHandler;

  @BeforeEach
  void setUp() {
    ownershipHandler = new OwnershipHandler(userRepository);
  }

  @Test
  void givenUserExists_whenMapDeliveryIdToTransactionId_thenMapsDeliveryToTransaction() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    ownershipHandler.mapDeliveryIdToTransactionId(A_SESSION, transactionId, deliveryId);

    // then
    verify(user).addTransactionDelivery(transactionId, deliveryId);
  }

  @Test
  public void givenTransactionAddedToUser_whenAddTransactionOwnership_thenUserIsUpdated() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    ownershipHandler.mapDeliveryIdToTransactionId(A_SESSION, transactionId, deliveryId);

    // then
    verify(userRepository).update(user);
  }

  @Test
  void givenUserIsOwnerOfTransaction_whenValidateTransactionOwnership_thenDoNothing() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);
    given(user.ownsTransaction(transactionId)).willReturn(true);

    // when
    Executable validatingOwnership =
        () -> ownershipHandler.validateTransactionOwnership(A_SESSION, transactionId);

    // then
    assertDoesNotThrow(validatingOwnership);
  }

  @Test
  void givenUserIsOwnerOfDelivery_whenValidateDeliveryOwnership_thenDoNothing() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);
    given(user.ownDelivery(deliveryId)).willReturn(true);

    // when
    Executable validatingOwnership =
        () -> ownershipHandler.validateDeliveryOwnership(A_SESSION, deliveryId);

    // then
    assertDoesNotThrow(validatingOwnership);
  }

  @Test
  void
      givenUserIsNotOwnerOfTransaction_whenValidateTransactionOwnership_thenThrowWrongOwnerException() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);
    given(user.ownsTransaction(transactionId)).willReturn(false);

    // when
    Executable validatingOwnership =
        () -> ownershipHandler.validateTransactionOwnership(A_SESSION, transactionId);

    // then
    assertThrows(WrongOwnerException.class, validatingOwnership);
  }

  @Test
  void givenUserIsNotOwnerOfDelivery_whenValidateDeliveryOwnership_thenThrowWrongOwnerException() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);
    given(user.ownDelivery(deliveryId)).willReturn(false);

    // when
    Executable validatingOwnership =
        () -> ownershipHandler.validateDeliveryOwnership(A_SESSION, deliveryId);

    // then
    assertThrows(WrongOwnerException.class, validatingOwnership);
  }

  @Test
  void whenGetTransactionIdFromDeliveryId_thenUserRepositoryFindUser() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    ownershipHandler.retrieveTransactionId(A_SESSION, deliveryId);

    // then
    verify(userRepository).findUser(A_SESSION.getEmail());
  }

  @Test
  void
      givenADeliveryMappedToATransaction_whenGetTransactionIdFromDeliveryId_thenReturnCorrectTransactionId() {
    // given
    User user = givenUserWithMappedDeliveryToTransaction(A_TRANSACTION_ID, A_DELIVERY_ID);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    TransactionId actualTransactionId =
        ownershipHandler.retrieveTransactionId(A_SESSION, A_DELIVERY_ID);

    // then
    assertThat(actualTransactionId).isEqualTo(A_TRANSACTION_ID);
  }

  private User givenUserWithMappedDeliveryToTransaction(
      TransactionId transactionId, DeliveryId deliveryId) {
    User user =
        new User("Sean", new BirthDate(LocalDate.of(1994, 6, 16)), "male", "sean@sean.sean");
    user.addTransactionDelivery(transactionId, deliveryId);
    return user;
  }
}
