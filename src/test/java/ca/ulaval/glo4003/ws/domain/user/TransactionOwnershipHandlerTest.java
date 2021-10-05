package ca.ulaval.glo4003.ws.domain.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionOwnershipHandlerTest {
  private static final String AN_EMAIL = "siaodjasod";
  private static final Session A_SESSION = new Session(new SessionToken("dosakda"), AN_EMAIL);

  @Mock private UserRepository userRepository;
  @Mock private User aUser;
  @Mock private TransactionId aTransactionId;

  private TransactionOwnershipHandler transactionOwnershipHandler;

  @BeforeEach
  public void setUp() {
    transactionOwnershipHandler = new TransactionOwnershipHandler(userRepository);
  }

  @Test
  public void givenUserExists_whenAddTransactionOwnership_thenAddTransactionToUser() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));

    // when
    transactionOwnershipHandler.addTransactionOwnership(A_SESSION, aTransactionId);

    // then
    verify(aUser).addTransaction(aTransactionId);
  }

  @Test
  public void givenTransactionAddedToUser_whenAddTransactionOwnership_thenSaveUser() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));

    // when
    transactionOwnershipHandler.addTransactionOwnership(A_SESSION, aTransactionId);

    // then
    verify(userRepository).update(aUser);
  }

  @Test
  public void givenUserIsOwnerOfTransaction_whenValidateOwnership_thenDoNothing() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));
    given(aUser.doesOwnTransaction(aTransactionId)).willReturn(true);

    // when
    Executable validatingOwnership =
        () -> transactionOwnershipHandler.validateOwnership(A_SESSION, aTransactionId);

    // then
    assertDoesNotThrow(validatingOwnership);
  }

  @Test
  public void
      givenUserIsNotOwnerOfTransaction_whenValidateOwnership_thenThrowWrongOwnerException() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));
    given(aUser.doesOwnTransaction(aTransactionId)).willReturn(false);

    // when
    Executable validatingOwnership =
        () -> transactionOwnershipHandler.validateOwnership(A_SESSION, aTransactionId);

    // then
    assertThrows(WrongOwnerException.class, validatingOwnership);
  }
}
