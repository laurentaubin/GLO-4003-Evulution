package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OwnershipDomainServiceTest {
  @Mock private User aUser;
  @Mock private TransactionId aTransactionId;
  @Mock private DeliveryId aDeliveryId;

  private OwnershipDomainService ownershipDomainService;

  @BeforeEach
  void setUp() {
    ownershipDomainService = new OwnershipDomainService();
  }

  @Test
  void givenUserIsOwnerOfTransaction_whenValidateTransactionOwnership_thenDoNothing() {
    // given
    given(aUser.ownsTransaction(aTransactionId)).willReturn(true);

    // when
    Executable validatingOwnership =
        () -> ownershipDomainService.validateTransactionOwnership(aUser, aTransactionId);

    // then
    assertDoesNotThrow(validatingOwnership);
  }

  @Test
  void givenUserIsOwnerOfDelivery_whenValidateDeliveryOwnership_thenDoNothing() {
    // given
    given(aUser.ownDelivery(aDeliveryId)).willReturn(true);

    // when
    Executable validatingOwnership =
        () -> ownershipDomainService.validateDeliveryOwnership(aUser, aDeliveryId);

    // then
    assertDoesNotThrow(validatingOwnership);
  }

  @Test
  void
      givenUserIsNotOwnerOfTransaction_whenValidateTransactionOwnership_thenThrowWrongOwnerException() {
    // given
    given(aUser.ownsTransaction(aTransactionId)).willReturn(false);

    // when
    Executable validatingOwnership =
        () -> ownershipDomainService.validateTransactionOwnership(aUser, aTransactionId);

    // then
    assertThrows(TransactionNotFoundException.class, validatingOwnership);
  }

  @Test
  void givenUserIsNotOwnerOfDelivery_whenValidateDeliveryOwnership_thenThrowWrongOwnerException() {
    // given
    given(aUser.ownDelivery(aDeliveryId)).willReturn(false);

    // when
    Executable validatingOwnership =
        () -> ownershipDomainService.validateDeliveryOwnership(aUser, aDeliveryId);

    // then
    assertThrows(DeliveryNotFoundException.class, validatingOwnership);
  }
}
