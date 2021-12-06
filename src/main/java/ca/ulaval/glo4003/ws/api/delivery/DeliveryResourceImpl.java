package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.service.delivery.dto.validator.DeliveryRequestValidator;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class DeliveryResourceImpl implements DeliveryResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final List<Role> PRIVILEGED_ROLES =
      new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  private final DeliveryService deliveryService;
  private final AuthenticationService authenticationService;
  private final DeliveryRequestValidator deliveryRequestValidator;

  public DeliveryResourceImpl() {
    this(
        serviceLocator.resolve(DeliveryService.class),
        serviceLocator.resolve(AuthenticationService.class),
        new DeliveryRequestValidator());
  }

  public DeliveryResourceImpl(
      DeliveryService deliveryService,
      AuthenticationService authenticationService,
      DeliveryRequestValidator deliveryRequestValidator) {
    this.deliveryService = deliveryService;
    this.authenticationService = authenticationService;
    this.deliveryRequestValidator = deliveryRequestValidator;
  }

  @Override
  public Response addDeliveryLocation(
      ContainerRequestContext containerRequestContext,
      DeliveryId deliveryId,
      DeliveryLocationRequest deliveryLocationRequest) {
    deliveryRequestValidator.validate(deliveryLocationRequest);
    authenticationService.validateDeliveryOwnership(
        containerRequestContext, deliveryId, PRIVILEGED_ROLES);

    deliveryService.addDeliveryLocation(deliveryId, deliveryLocationRequest);

    return Response.accepted().build();
  }

  @Override
  public Response completeDelivery(
      ContainerRequestContext containerRequestContext, DeliveryId deliveryId) {
    authenticationService.validateDeliveryOwnership(
        containerRequestContext, deliveryId, PRIVILEGED_ROLES);
    Session session =
        authenticationService.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
    TransactionId transactionId =
        authenticationService.retrieveTransactionIdFromSession(session, deliveryId);

    CompletedDeliveryResponse deliveryResponse = deliveryService.completeDelivery(transactionId);

    return Response.ok().entity(deliveryResponse).build();
  }
}
