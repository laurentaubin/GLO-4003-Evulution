package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.*;
import ca.ulaval.glo4003.ws.service.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.service.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.service.transaction.dto.validators.VehicleRequestValidator;
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
  private final VehicleRequestValidator vehicleRequestValidator;
  private final BatteryRequestValidator batteryRequestValidator;
  private final PaymentRequestValidator paymentRequestValidator;

  public TransactionResourceImpl() {
    this(
        serviceLocator.resolve(TransactionService.class),
        serviceLocator.resolve(AuthenticationService.class),
        new VehicleRequestValidator(),
        new BatteryRequestValidator(),
        new PaymentRequestValidator());
  }

  public TransactionResourceImpl(
      TransactionService transactionService,
      AuthenticationService authenticationService,
      VehicleRequestValidator vehicleRequestValidator,
      BatteryRequestValidator batteryRequestValidator,
      PaymentRequestValidator paymentRequestValidator) {
    this.transactionService = transactionService;
    this.authenticationService = authenticationService;
    this.vehicleRequestValidator = vehicleRequestValidator;
    this.batteryRequestValidator = batteryRequestValidator;
    this.paymentRequestValidator = paymentRequestValidator;
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
    vehicleRequestValidator.validate(vehicleRequest);
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
    batteryRequestValidator.validate(batteryRequest);
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
    paymentRequestValidator.validate(paymentRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);

    transactionService.completeTransaction(transactionId, paymentRequest);

    return Response.ok().build();
  }
}
