package ca.ulaval.glo4003.ws.domain.delivery;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryOwnershipHandlerTest {
  private static final String AN_EMAIL = "siaodjasod";
  private static final Session A_SESSION = new Session(new SessionToken("dosakda"), AN_EMAIL);

  @Mock private UserRepository userRepository;
  @Mock private User aUser;
  @Mock private DeliveryId aDeliveryId;

  private DeliveryOwnershipHandler deliveryOwnershipHandler;

  @BeforeEach
  public void setUp() {
    deliveryOwnershipHandler = new DeliveryOwnershipHandler(userRepository);
  }

  @Test
  public void givenUserExists_whenAddDeliveryOwnership_thenAddDeliveryToUser() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(aUser);

    // when
    deliveryOwnershipHandler.addDeliveryOwnership(A_SESSION, aDeliveryId);

    // then
    verify(aUser).addDelivery(aDeliveryId);
  }

  @Test
  public void givenDeliveryAddedToUser_whenAddDeliveryOwnership_thenSaveUser() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(aUser);

    // when
    deliveryOwnershipHandler.addDeliveryOwnership(A_SESSION, aDeliveryId);

    // then
    verify(userRepository).update(aUser);
  }

  @Test
  public void givenUserIsOwnerOfDelivery_whenValidateOwnership_thenDoNothing() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(aUser);
    given(aUser.doesOwnDelivery(aDeliveryId)).willReturn(true);

    // when
    Executable validatingOwnership =
        () -> deliveryOwnershipHandler.validateOwnership(A_SESSION, aDeliveryId);

    // then
    assertDoesNotThrow(validatingOwnership);
  }

  @Test
  public void givenUserIsNotOwnerOfDelivery_whenValidateOwnership_thenThrowWrongOwnerException() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(aUser);
    given(aUser.doesOwnDelivery(aDeliveryId)).willReturn(false);

    // when
    Executable validatingOwnership =
        () -> deliveryOwnershipHandler.validateOwnership(A_SESSION, aDeliveryId);

    // then
    assertThrows(WrongOwnerException.class, validatingOwnership);
  }
}
