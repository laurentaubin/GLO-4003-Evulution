package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

public class DeliveryResourceImpl implements DeliveryResource {
  public static final String ADD_DELIVERY_MESSAGE = "Delivery location successfully added";
  private static final List<Role> PRIVILEGED_ROLES =
      new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  private final DeliveryService deliveryService;
  private final DeliveryRequestValidator deliveryRequestValidator;
  private final DeliveryDestinationAssembler deliveryDestinationAssembler;
  private final CompletedDeliveryResponseAssembler completedDeliveryResponseAssembler;
  private final OwnershipHandler ownershipHandler;
  private final RoleHandler roleHandler;

  public DeliveryResourceImpl(
      DeliveryService deliveryService,
      DeliveryRequestValidator deliveryRequestValidator,
      DeliveryDestinationAssembler deliveryDestinationAssembler,
      CompletedDeliveryResponseAssembler completedDeliveryResponseAssembler,
      OwnershipHandler ownershipHandler,
      RoleHandler roleHandler) {
    this.deliveryService = deliveryService;
    this.deliveryRequestValidator = deliveryRequestValidator;
    this.deliveryDestinationAssembler = deliveryDestinationAssembler;
    this.completedDeliveryResponseAssembler = completedDeliveryResponseAssembler;
    this.ownershipHandler = ownershipHandler;
    this.roleHandler = roleHandler;
  }

  @Override
  public Response addDeliveryLocation(
      ContainerRequestContext containerRequestContext,
      String deliveryId,
      DeliveryLocationRequest deliveryLocationRequest) {
    DeliveryId serializedDeliveryId = new DeliveryId(deliveryId);

    deliveryRequestValidator.validate(deliveryLocationRequest);
    Session userSession = roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);

    validateDeliveryOwnership(userSession, serializedDeliveryId);

    DeliveryDestination deliveryDestination =
        deliveryDestinationAssembler.assemble(deliveryLocationRequest);
    deliveryService.addDeliveryDestination(serializedDeliveryId, deliveryDestination);
    return Response.accepted().entity(ADD_DELIVERY_MESSAGE).build();
  }

  @Override
  public Response completeDelivery(
      ContainerRequestContext containerRequestContext, String deliveryId) {
    DeliveryId serializedDeliveryId = new DeliveryId(deliveryId);
    Session userSession = roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);

    validateDeliveryOwnership(userSession, serializedDeliveryId);
    TransactionId transactionIdLinkedToDelivery =
        ownershipHandler.retrieveTransactionId(userSession, serializedDeliveryId);

    Receipt receipt = deliveryService.generateTransactionReceipt(transactionIdLinkedToDelivery);
    CompletedDeliveryResponse completedDeliveryResponse =
        completedDeliveryResponseAssembler.assemble(
            receipt.getAmountPerPeriod().toInt(), receipt.getPaymentsLeft());
    return Response.ok().entity(completedDeliveryResponse).build();
  }

  private void validateDeliveryOwnership(Session userSession, DeliveryId deliveryId) {

    try {
      ownershipHandler.validateDeliveryOwnership(userSession, deliveryId);

    } catch (WrongOwnerException ignored) {
      throw new DeliveryNotFoundException(deliveryId);
    }
  }
}
