package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.*;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.api.validator.RoleValidator;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {

  private final TransactionService transactionService;
  private final DeliveryService deliveryService;
  private final CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  private final VehicleRequestAssembler vehicleRequestAssembler;
  private final VehicleRequestValidator vehicleRequestValidator;
  private final RoleValidator roleValidator;
  private static final List<Role> privilegedRoles = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));
  private final BatteryRequestValidator batteryRequestValidator;
  private final PaymentRequestAssembler paymentRequestAssembler;
  private final PaymentRequestValidator paymentRequestValidator;

  public TransactionResourceImpl(
      TransactionService transactionService,
      DeliveryService deliveryService,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      VehicleRequestAssembler vehicleRequestAssembler,
      VehicleRequestValidator vehicleRequestValidator,
      RoleValidator roleValidator,
      BatteryRequestValidator batteryRequestValidator,
      PaymentRequestAssembler paymentRequestAssembler,
      PaymentRequestValidator paymentRequestValidator) {
    this.transactionService = transactionService;
    this.deliveryService = deliveryService;
    this.createdTransactionResponseAssembler = createdTransactionResponseAssembler;
    this.vehicleRequestAssembler = vehicleRequestAssembler;
    this.vehicleRequestValidator = vehicleRequestValidator;
    this.roleValidator = roleValidator;
    this.batteryRequestValidator = batteryRequestValidator;
    this.paymentRequestAssembler = paymentRequestAssembler;
    this.paymentRequestValidator = paymentRequestValidator;
  }

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    roleValidator.validate(containerRequestContext, privilegedRoles);
    Transaction transaction = transactionService.createTransaction();
    Delivery delivery = deliveryService.createDelivery();
    CreatedTransactionResponse createdTransactionResponse =
        createdTransactionResponseAssembler.assemble(transaction, delivery);
    URI transactionUri = URI.create(String.format("/sales/%s", transaction.getId()));
    return Response.created(transactionUri).entity(createdTransactionResponse).build();
  }

  @Override
  public Response addVehicle(
      ContainerRequestContext containerRequestContext,
      String transactionId,
      VehicleRequest vehicleRequest) {
    roleValidator.validate(containerRequestContext, privilegedRoles);
    vehicleRequestValidator.validate(vehicleRequest);
    Vehicle vehicle = vehicleRequestAssembler.create(vehicleRequest);
    transactionService.addVehicle(TransactionId.fromString(transactionId), vehicle);
    return Response.accepted().build();
  }

  @Override
  public Response addBattery(String transactionId, BatteryRequest batteryRequest) {
    batteryRequestValidator.validate(batteryRequest);
    String batteryRequestType = batteryRequest.getType();
    Transaction transaction =
        transactionService.addBattery(TransactionId.fromString(transactionId), batteryRequestType);
    Integer batteryEstimatedRange = transaction.computeRange();
    AddedBatteryResponse batteryResponse = new AddedBatteryResponse(batteryEstimatedRange);
    return Response.accepted().entity(batteryResponse).build();
  }

  @Override
  public Response completeTransaction(String transactionId, PaymentRequest paymentRequest) {
    paymentRequestValidator.validate(paymentRequest);
    Payment payment = paymentRequestAssembler.create(paymentRequest);
    transactionService.addPayment(TransactionId.fromString(transactionId), payment);
    return Response.ok().build();
  }
}
