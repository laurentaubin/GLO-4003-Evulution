package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
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
  private final DeliveryOwnershipHandler deliveryOwnershipHandler;
  private final RoleHandler roleHandler;

  public DeliveryResourceImpl(
      DeliveryService deliveryService,
      DeliveryRequestValidator deliveryRequestValidator,
      DeliveryDestinationAssembler deliveryDestinationAssembler,
      CompletedDeliveryResponseAssembler completedDeliveryResponseAssembler,
      DeliveryOwnershipHandler deliveryOwnershipHandler,
      RoleHandler roleHandler) {
    this.deliveryService = deliveryService;
    this.deliveryRequestValidator = deliveryRequestValidator;
    this.deliveryDestinationAssembler = deliveryDestinationAssembler;
    this.completedDeliveryResponseAssembler = completedDeliveryResponseAssembler;
    this.deliveryOwnershipHandler = deliveryOwnershipHandler;
    this.roleHandler = roleHandler;
  }

  @Override
  public Response addDeliveryLocation(
      ContainerRequestContext containerRequestContext,
      String deliveryId,
      DeliveryLocationRequest deliveryLocationRequest) {
    DeliveryId serializedDeliveryId = new DeliveryId(deliveryId);

    deliveryRequestValidator.validate(deliveryLocationRequest);
    validateDeliveryOwnership(containerRequestContext, serializedDeliveryId);

    DeliveryDestination deliveryDestination =
        deliveryDestinationAssembler.assemble(deliveryLocationRequest);
    deliveryService.addDeliveryDestination(serializedDeliveryId, deliveryDestination);
    return Response.accepted().entity(ADD_DELIVERY_MESSAGE).build();
  }

  @Override
  public Response completeDelivery(
      ContainerRequestContext containerRequestContext, String deliveryId) {
    DeliveryId serializedDeliveryId = new DeliveryId(deliveryId);

    validateDeliveryOwnership(containerRequestContext, serializedDeliveryId);

    CompletedDeliveryResponse completedDeliveryResponse =
        completedDeliveryResponseAssembler.assemble();

    return Response.ok().entity(completedDeliveryResponse).build();
  }

  private void validateDeliveryOwnership(
      ContainerRequestContext containerRequestContext, DeliveryId deliveryId) {
    Session userSession = roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
    try {
      deliveryOwnershipHandler.validateOwnership(userSession, deliveryId);

    } catch (WrongOwnerException ignored) {
      throw new DeliveryNotFoundException(deliveryId);
    }
  }
}
