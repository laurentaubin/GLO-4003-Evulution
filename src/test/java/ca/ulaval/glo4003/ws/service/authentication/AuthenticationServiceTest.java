package ca.ulaval.glo4003.ws.service.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  private static final List<Role> ROLES = new ArrayList<>(List.of(Role.BASE));
  private static final String AN_ID = "id";
  private static final TransactionId TRANSACTION_ID = TransactionId.fromString("id");
  private static final DeliveryId DELIVERY_ID = DeliveryId.fromString("id");

  @Mock private OwnershipHandler ownershipHandler;
  @Mock private RoleHandler roleHandler;
  @Mock private ContainerRequestContext requestContext;
  @Mock private Session aSession;

  private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    authenticationService = new AuthenticationService(ownershipHandler, roleHandler);
  }

  @Test
  public void whenRetrieveSession_retrieveSessionFromRoleHandlerCalled() {
    // when
    authenticationService.retrieveSession(requestContext, ROLES);

    // then
    verify(roleHandler).retrieveSession(requestContext, ROLES);
  }

  @Test
  public void retrieveTransactionIdFromSession_retrieveSessionFromRoleHandlerCalled() {
    // when
    authenticationService.retrieveTransactionIdFromSession(aSession, DELIVERY_ID);

    // then
    verify(ownershipHandler).retrieveTransactionId(aSession, DELIVERY_ID);
  }

  @Test
  public void givenNotOwnedDelivery_whenValidateDeliveryOwnership_thenExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, ROLES)).willReturn(aSession);
    doThrow(WrongOwnerException.class)
        .when(ownershipHandler)
        .validateDeliveryOwnership(aSession, DELIVERY_ID);

    // when
    Executable action =
        () -> authenticationService.validateDeliveryOwnership(requestContext, DELIVERY_ID, ROLES);

    // then
    assertThrows(DeliveryNotFoundException.class, action);
  }

  @Test
  public void givenOwnedDelivery_whenValidateDeliveryOwnership_thenNoExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, ROLES)).willReturn(aSession);

    // when
    Executable action =
        () -> authenticationService.validateDeliveryOwnership(requestContext, DELIVERY_ID, ROLES);

    // then
    assertDoesNotThrow(action);
  }

  @Test
  public void givenNotOwnedTransaction_whenValidateTransactionOwnership_thenExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, ROLES)).willReturn(aSession);
    doThrow(WrongOwnerException.class)
        .when(ownershipHandler)
        .validateTransactionOwnership(aSession, TRANSACTION_ID);

    // when
    Executable action =
        () ->
            authenticationService.validateTransactionOwnership(
                requestContext, TRANSACTION_ID, ROLES);

    // then
    assertThrows(TransactionNotFoundException.class, action);
  }

  @Test
  public void givenOwnedTransaction_whenValidateTransactionOwnership_thenNoExceptionThrown() {
    // given
    given(roleHandler.retrieveSession(requestContext, ROLES)).willReturn(aSession);

    // when
    Executable action =
        () ->
            authenticationService.validateTransactionOwnership(
                requestContext, TRANSACTION_ID, ROLES);

    // then
    assertDoesNotThrow(action);
  }

  @Test
  public void whenMapDeliveryIdToTransactionId_thenMappingCalled() {
    // when
    authenticationService.mapDeliveryIdToTransactionId(aSession, AN_ID, AN_ID);

    // then
    verify(ownershipHandler).mapDeliveryIdToTransactionId(aSession, TRANSACTION_ID, DELIVERY_ID);
  }
}
