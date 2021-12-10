package ca.ulaval.glo4003.ws.service.authentication;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  private static final List<Role> SOME_ROLES = new ArrayList<>(List.of(Role.BASE));
  private static final String AN_ID = "id";
  private static final TransactionId A_TRANSACTION_ID = TransactionId.fromString("id");
  private static final DeliveryId A_DELIVERY_ID = DeliveryId.fromString("id");

  @Mock private OwnershipHandler ownershipHandler;
  @Mock private RoleHandler roleHandler;
  @Mock private ContainerRequestContext requestContext;
  @Mock private Session session;

  private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    authenticationService = new AuthenticationService(ownershipHandler, roleHandler);
  }

  @Test
  public void whenRetrieveSession_retrieveSessionFromRoleHandlerCalled() {
    // when
    authenticationService.retrieveSession(requestContext, SOME_ROLES);

    // then
    verify(roleHandler).retrieveSession(requestContext, SOME_ROLES);
  }

  @Test
  public void retrieveTransactionIdFromSession_retrieveSessionFromRoleHandlerCalled() {
    // when
    authenticationService.retrieveTransactionIdFromSession(session, A_DELIVERY_ID);

    // then
    verify(ownershipHandler).retrieveTransactionId(session, A_DELIVERY_ID);
  }

  @Test
  public void givenNotOwnedDelivery_whenValidateDeliveryOwnership_thenExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, SOME_ROLES)).willReturn(session);
    doThrow(WrongOwnerException.class)
        .when(ownershipHandler)
        .validateDeliveryOwnership(session, A_DELIVERY_ID);

    // when
    Executable action =
        () ->
            authenticationService.validateDeliveryOwnership(
                requestContext, A_DELIVERY_ID, SOME_ROLES);

    // then
    assertThrows(DeliveryNotFoundException.class, action);
  }

  @Test
  public void givenOwnedDelivery_whenValidateDeliveryOwnership_thenNoExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, SOME_ROLES)).willReturn(session);

    // when
    Executable action =
        () ->
            authenticationService.validateDeliveryOwnership(
                requestContext, A_DELIVERY_ID, SOME_ROLES);

    // then
    assertDoesNotThrow(action);
  }

  @Test
  public void givenNotOwnedTransaction_whenValidateTransactionOwnership_thenExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, SOME_ROLES)).willReturn(session);
    doThrow(WrongOwnerException.class)
        .when(ownershipHandler)
        .validateTransactionOwnership(session, A_TRANSACTION_ID);

    // when
    Executable action =
        () ->
            authenticationService.validateTransactionOwnership(
                requestContext, A_TRANSACTION_ID, SOME_ROLES);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void givenOwnedTransaction_whenValidateTransactionOwnership_thenNoExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, SOME_ROLES)).willReturn(session);

    // when
    Executable action =
        () ->
            authenticationService.validateTransactionOwnership(
                requestContext, A_TRANSACTION_ID, SOME_ROLES);

    // then
    assertDoesNotThrow(action);
  }

  @Test
  public void whenMapDeliveryIdToTransactionId_thenMappingCalled() {
    // when
    authenticationService.mapDeliveryIdToTransactionId(session, AN_ID, AN_ID);

    // then
    verify(ownershipHandler).mapDeliveryIdToTransactionId(session, A_TRANSACTION_ID, A_DELIVERY_ID);
  }
}
