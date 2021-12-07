package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryResponse;
import ca.ulaval.glo4003.ws.service.transaction.dto.CreatedTransactionResponse;
import ca.ulaval.glo4003.ws.service.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.VehicleRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final List<Role> PRIVILEGED_ROLES =
      new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  private final TransactionService transactionService;
  private final AuthenticationService authenticationService;
  private final RequestValidator requestValidator;

  public TransactionResourceImpl() {
    this(
        serviceLocator.resolve(TransactionService.class),
        serviceLocator.resolve(AuthenticationService.class),
        new RequestValidator());
  }

  public TransactionResourceImpl(
      TransactionService transactionService,
      AuthenticationService authenticationService,
      RequestValidator requestValidator) {
    this.transactionService = transactionService;
    this.authenticationService = authenticationService;
    this.requestValidator = requestValidator;
  }

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    Session userSession =
        authenticationService.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
    CreatedTransactionResponse createdTransaction = transactionService.createTransaction();

    authenticationService.mapDeliveryIdToTransactionId(
        userSession, createdTransaction.transactionId, createdTransaction.deliveryId);

    URI transactionUri = URI.create(String.format("/sales/%s", createdTransaction.transactionId));
    return Response.created(transactionUri).entity(createdTransaction).build();
  }

  @Override
  public Response addVehicle(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      VehicleRequest vehicleRequest) {
    requestValidator.validate(vehicleRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);

    transactionService.addVehicle(transactionId, vehicleRequest);

    return Response.accepted().build();
  }

  @Override
  public Response addBattery(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      BatteryRequest batteryRequest) {
    requestValidator.validate(batteryRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);

    BatteryResponse batteryResponse = transactionService.addBattery(transactionId, batteryRequest);

    return Response.accepted().entity(batteryResponse).build();
  }

  @Override
  public Response completeTransaction(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      PaymentRequest paymentRequest) {
    requestValidator.validate(paymentRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);

    transactionService.completeTransaction(transactionId, paymentRequest);

    return Response.ok().build();
  }
}
