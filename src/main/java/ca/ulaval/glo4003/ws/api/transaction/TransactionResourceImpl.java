package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.transaction.dto.*;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.TransactionOwnershipHandler;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {

  private final TransactionService transactionService;
  private final DeliveryService deliveryService;
  private final TransactionOwnershipHandler transactionOwnershipHandler;
  private final CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  private final VehicleRequestAssembler vehicleRequestAssembler;
  private final VehicleRequestValidator vehicleRequestValidator;
  private final RoleHandler roleHandler;
  private static final List<Role> privilegedRoles = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));
  private final BatteryRequestValidator batteryRequestValidator;
  private final PaymentRequestAssembler paymentRequestAssembler;
  private final PaymentRequestValidator paymentRequestValidator;

  public TransactionResourceImpl(
      TransactionService transactionService,
      DeliveryService deliveryService,
      TransactionOwnershipHandler transactionOwnershipHandler,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      VehicleRequestAssembler vehicleRequestAssembler,
      VehicleRequestValidator vehicleRequestValidator,
      RoleHandler roleHandler,
      BatteryRequestValidator batteryRequestValidator,
      PaymentRequestAssembler paymentRequestAssembler,
      PaymentRequestValidator paymentRequestValidator) {
    this.transactionService = transactionService;
    this.deliveryService = deliveryService;
    this.transactionOwnershipHandler = transactionOwnershipHandler;
    this.createdTransactionResponseAssembler = createdTransactionResponseAssembler;
    this.vehicleRequestAssembler = vehicleRequestAssembler;
    this.vehicleRequestValidator = vehicleRequestValidator;
    this.roleHandler = roleHandler;
    this.batteryRequestValidator = batteryRequestValidator;
    this.paymentRequestAssembler = paymentRequestAssembler;
    this.paymentRequestValidator = paymentRequestValidator;
  }

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    Session userSession = roleHandler.retrieveSession(containerRequestContext, privilegedRoles);
    Transaction createdTransaction = transactionService.createTransaction();
    transactionOwnershipHandler.addTransactionOwnership(userSession, createdTransaction.getId());
    Delivery delivery = deliveryService.createDelivery();
    CreatedTransactionResponse createdTransactionResponse =
        createdTransactionResponseAssembler.assemble(createdTransaction, delivery);
    URI transactionUri = URI.create(String.format("/sales/%s", createdTransaction.getId()));
    return Response.created(transactionUri).entity(createdTransactionResponse).build();
  }

  @Override
  public Response addVehicle(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      VehicleRequest vehicleRequest) {
    vehicleRequestValidator.validate(vehicleRequest);
    Session userSession = roleHandler.retrieveSession(containerRequestContext, privilegedRoles);
    transactionOwnershipHandler.validateOwnership(userSession, new TransactionId(transactionId));
    Vehicle vehicle = vehicleRequestAssembler.create(vehicleRequest);
    transactionService.addVehicle(TransactionId.fromString(transactionId), vehicle);
    return Response.accepted().build();
  }

  @Override
  public Response addBattery(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      BatteryRequest batteryRequest) {
    batteryRequestValidator.validate(batteryRequest);
    Session userSession = roleHandler.retrieveSession(containerRequestContext, privilegedRoles);
    transactionOwnershipHandler.validateOwnership(userSession, new TransactionId(transactionId));
    String batteryRequestType = batteryRequest.getType();
    Transaction transaction =
        transactionService.addBattery(TransactionId.fromString(transactionId), batteryRequestType);
    Integer batteryEstimatedRange = transaction.computeRange();
    AddedBatteryResponse batteryResponse = new AddedBatteryResponse(batteryEstimatedRange);
    return Response.accepted().entity(batteryResponse).build();
  }

  @Override
  public Response completeTransaction(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      PaymentRequest paymentRequest) {
    paymentRequestValidator.validate(paymentRequest);
    Session userSession = roleHandler.retrieveSession(containerRequestContext, privilegedRoles);
    transactionOwnershipHandler.validateOwnership(userSession, new TransactionId(transactionId));
    Payment payment = paymentRequestAssembler.create(paymentRequest);
    transactionService.addPayment(TransactionId.fromString(transactionId), payment);
    return Response.ok().build();
  }
}
