package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.AddedBatteryResponse;
import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.CreatedTransactionResponse;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.api.validator.RoleValidator;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.Vehicle;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {

  private TransactionService transactionService;
  private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  private VehicleRequestAssembler vehicleRequestAssembler;
  private VehicleRequestValidator vehicleRequestValidator;
  private RoleValidator roleValidator;
  private static final List<Role> privilegedRoles = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));
  private BatteryRequestValidator batteryRequestValidator;

  public TransactionResourceImpl(
      TransactionService transactionService,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      VehicleRequestAssembler vehicleRequestAssembler,
      VehicleRequestValidator vehicleRequestValidator,
      RoleValidator roleValidator,
      BatteryRequestValidator batteryRequestValidator) {
    this.transactionService = transactionService;
    this.createdTransactionResponseAssembler = createdTransactionResponseAssembler;
    this.vehicleRequestAssembler = vehicleRequestAssembler;
    this.vehicleRequestValidator = vehicleRequestValidator;
    this.roleValidator = roleValidator;
    this.batteryRequestValidator = batteryRequestValidator;
  }

  public TransactionResourceImpl(
      TransactionService transactionService,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      VehicleRequestAssembler vehicleRequestAssembler,
      VehicleRequestValidator vehicleRequestValidator,
      RoleValidator roleValidator) {}

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    roleValidator.validate(containerRequestContext, privilegedRoles);
    Transaction transaction = transactionService.createTransaction();
    CreatedTransactionResponse createdTransactionResponse =
        createdTransactionResponseAssembler.create(transaction);
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
}
