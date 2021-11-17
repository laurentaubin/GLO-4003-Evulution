package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.transaction.dto.*;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {
  private static final List<Role> PRIVILEGED_ROLES =
      new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  private final TransactionService transactionService;
  private final DeliveryService deliveryService;
  private final RoleHandler roleHandler;
  private final OwnershipHandler ownershipHandler;
  private final CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  private final VehicleRequestValidator vehicleRequestValidator;
  private final BatteryRequestValidator batteryRequestValidator;
  private final PaymentRequestAssembler paymentRequestAssembler;
  private final PaymentRequestValidator paymentRequestValidator;
  private final VehicleFactory vehicleFactory;

  public TransactionResourceImpl(
      TransactionService transactionService,
      DeliveryService deliveryService,
      OwnershipHandler ownershipHandler,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      VehicleRequestValidator vehicleRequestValidator,
      RoleHandler roleHandler,
      BatteryRequestValidator batteryRequestValidator,
      PaymentRequestAssembler paymentRequestAssembler,
      PaymentRequestValidator paymentRequestValidator,
      VehicleFactory vehicleFactory) {
    this.transactionService = transactionService;
    this.deliveryService = deliveryService;
    this.ownershipHandler = ownershipHandler;
    this.createdTransactionResponseAssembler = createdTransactionResponseAssembler;
    this.vehicleRequestValidator = vehicleRequestValidator;
    this.roleHandler = roleHandler;
    this.batteryRequestValidator = batteryRequestValidator;
    this.paymentRequestAssembler = paymentRequestAssembler;
    this.paymentRequestValidator = paymentRequestValidator;
    this.vehicleFactory = vehicleFactory;
  }

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    Session userSession = roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
    Transaction createdTransaction = transactionService.createTransaction();
    Delivery createdDelivery = createDelivery(userSession, createdTransaction.getId());

    CreatedTransactionResponse createdTransactionResponse =
        createdTransactionResponseAssembler.assemble(createdTransaction, createdDelivery);

    URI transactionUri = URI.create(String.format("/sales/%s", createdTransaction.getId()));
    return Response.created(transactionUri).entity(createdTransactionResponse).build();
  }

  @Override
  public Response addVehicle(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      VehicleRequest vehicleRequest) {
    vehicleRequestValidator.validate(vehicleRequest);

    validateTransactionOwnership(containerRequestContext, new TransactionId(transactionId));
    transactionService.addVehicle(
        TransactionId.fromString(transactionId),
        vehicleFactory.create(vehicleRequest.getModel(), vehicleRequest.getColor()));
    return Response.accepted().build();
  }

  @Override
  public Response addBattery(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      BatteryRequest batteryRequest) {
    batteryRequestValidator.validate(batteryRequest);
    validateTransactionOwnership(containerRequestContext, new TransactionId(transactionId));

    Transaction transaction =
        transactionService.addBattery(
            TransactionId.fromString(transactionId), batteryRequest.getType());
    BigDecimal batteryEstimatedRange =
        transaction.computeEstimatedVehicleRange().setScale(2, RoundingMode.HALF_UP);
    AddedBatteryResponse batteryResponse = new AddedBatteryResponse(batteryEstimatedRange);
    return Response.accepted().entity(batteryResponse).build();
  }

  @Override
  public Response completeTransaction(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      PaymentRequest paymentRequest) {
    paymentRequestValidator.validate(paymentRequest);
    validateTransactionOwnership(containerRequestContext, new TransactionId(transactionId));

    Payment payment = paymentRequestAssembler.create(paymentRequest);
    transactionService.addPayment(TransactionId.fromString(transactionId), payment);
    return Response.ok().build();
  }

  private Delivery createDelivery(Session userSession, TransactionId transactionId) {
    Delivery createdDelivery = deliveryService.createDelivery();
    ownershipHandler.mapDeliveryIdToTransactionId(
        userSession, transactionId, createdDelivery.getDeliveryId());
    return createdDelivery;
  }

  private void validateTransactionOwnership(
      ContainerRequestContext containerRequestContext, TransactionId transactionId) {
    Session userSession = roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
    try {
      ownershipHandler.validateTransactionOwnership(userSession, transactionId);

    } catch (WrongOwnerException ignored) {
      throw new TransactionNotFoundException(transactionId);
    }
  }
}
