package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.CreatedTransactionResponse;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.Vehicle;
import jakarta.ws.rs.core.Response;
import java.net.URI;

public class TransactionResourceImpl implements TransactionResource {

  private TransactionService transactionService;
  private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  private VehicleRequestAssembler vehicleRequestAssembler;
  private VehicleRequestValidator vehicleRequestValidator;

  public TransactionResourceImpl(
      TransactionService transactionService,
      CreatedTransactionResponseAssembler createdTransactionResponseAssembler,
      VehicleRequestAssembler vehicleRequestAssembler,
      VehicleRequestValidator vehicleRequestValidator) {
    this.transactionService = transactionService;
    this.createdTransactionResponseAssembler = createdTransactionResponseAssembler;
    this.vehicleRequestAssembler = vehicleRequestAssembler;
    this.vehicleRequestValidator = vehicleRequestValidator;
  }

  @Override
  public Response createTransaction() {
    Transaction transaction = transactionService.createTransaction();
    CreatedTransactionResponse createdTransactionResponse =
        createdTransactionResponseAssembler.create(transaction);
    URI transactionUri = URI.create(String.format("/sales/%s", transaction.getId()));
    return Response.created(transactionUri).entity(createdTransactionResponse).build();
  }

  @Override
  public Response addVehicle(String transactionId, VehicleRequest vehicleRequest) {
    vehicleRequestValidator.validate(vehicleRequest);
    Vehicle vehicle = vehicleRequestAssembler.create(vehicleRequest);
    transactionService.addVehicle(TransactionId.fromString(transactionId), vehicle);
    return Response.accepted().build();
  }
}
