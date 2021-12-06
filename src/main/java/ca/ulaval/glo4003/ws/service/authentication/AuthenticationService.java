package ca.ulaval.glo4003.ws.service.authentication;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.List;

public class AuthenticationService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final OwnershipHandler ownershipHandler;
  private final RoleHandler roleHandler;

  public AuthenticationService() {
    this(serviceLocator.resolve(OwnershipHandler.class), serviceLocator.resolve(RoleHandler.class));
  }

  public AuthenticationService(OwnershipHandler ownershipHandler, RoleHandler roleHandler) {
    this.ownershipHandler = ownershipHandler;
    this.roleHandler = roleHandler;
  }

  public Session retrieveSession(
      ContainerRequestContext containerRequestContext, List<Role> roles) {
    return roleHandler.retrieveSession(containerRequestContext, roles);
  }

  public TransactionId retrieveTransactionIdFromSession(
      Session userSession, DeliveryId deliveryId) {
    return ownershipHandler.retrieveTransactionId(userSession, deliveryId);
  }

  public void mapDeliveryIdToTransactionId(
      Session userSession, String transactionId, String deliveryId) {
    ownershipHandler.mapDeliveryIdToTransactionId(
        userSession, TransactionId.fromString(transactionId), DeliveryId.fromString(deliveryId));
  }

  public void validateDeliveryOwnership(
      ContainerRequestContext containerRequestContext, DeliveryId deliveryId, List<Role> roles) {
    Session userSession = roleHandler.retrieveSession(containerRequestContext, roles);
    try {
      ownershipHandler.validateDeliveryOwnership(userSession, deliveryId);
    } catch (WrongOwnerException ignored) {
      throw new DeliveryNotFoundException(deliveryId);
    }
  }

  public void validateTransactionOwnership(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      List<Role> roles) {
    Session userSession = roleHandler.retrieveSession(containerRequestContext, roles);
    try {
      ownershipHandler.validateTransactionOwnership(userSession, transactionId);
    } catch (WrongOwnerException ignored) {
      throw new TransactionNotFoundException(transactionId);
    }
  }
}
