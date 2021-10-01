package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class DeliveryResourceImpl implements DeliveryResource {
  public static final String ADD_DELIVERY_MESSAGE = "Transaction complete";
  private static final List<Role> privilegedRoles = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  private final DeliveryService deliveryService;
  private final DeliveryRequestValidator deliveryRequestValidator;
  private final DeliveryDestinationAssembler deliveryDestinationAssembler;
  private final DeliveryOwnershipHandler deliveryOwnershipHandler;
  private final RoleHandler roleHandler;

  public DeliveryResourceImpl(
      DeliveryService deliveryService,
      DeliveryRequestValidator deliveryRequestValidator,
      DeliveryDestinationAssembler deliveryDestinationAssembler,
      DeliveryOwnershipHandler deliveryOwnershipHandler,
      RoleHandler roleHandler) {
    this.deliveryService = deliveryService;
    this.deliveryRequestValidator = deliveryRequestValidator;
    this.deliveryDestinationAssembler = deliveryDestinationAssembler;
    this.deliveryOwnershipHandler = deliveryOwnershipHandler;
    this.roleHandler = roleHandler;
  }

  @Override
  public Response addDeliveryLocation(
      ContainerRequestContext containerRequestContext,
      String deliveryId,
      DeliveryLocationRequest deliveryLocationRequest) {

    deliveryRequestValidator.validate(deliveryLocationRequest);
    Session userSession = roleHandler.retrieveSession(containerRequestContext, privilegedRoles);
    deliveryOwnershipHandler.validateOwnership(userSession, new DeliveryId(deliveryId));

    DeliveryDestination deliveryDestination =
        deliveryDestinationAssembler.assemble(deliveryLocationRequest);
    deliveryService.addDeliveryDestination(new DeliveryId(deliveryId), deliveryDestination);
    return Response.ok().entity(ADD_DELIVERY_MESSAGE).build();
  }
}
