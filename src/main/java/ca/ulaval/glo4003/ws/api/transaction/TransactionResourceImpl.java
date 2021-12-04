package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryResponse;
import ca.ulaval.glo4003.ws.api.transaction.dto.CreatedTransactionResponse;
import ca.ulaval.glo4003.ws.api.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccountFactory;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {
  private static final List<Role> PRIVILEGED_ROLES =
      new ArrayList<>(List.of(Role.BASE, Role.ADMIN));
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

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
  private final BatteryResponseAssembler batteryResponseAssembler;

  public TransactionResourceImpl() {
    this(
        serviceLocator.resolve(TransactionService.class),
        serviceLocator.resolve(DeliveryService.class),
        serviceLocator.resolve(OwnershipHandler.class),
        new CreatedTransactionResponseAssembler(),
        new VehicleRequestValidator(),
        serviceLocator.resolve(RoleHandler.class),
        new BatteryRequestValidator(),
        new PaymentRequestAssembler(new BankAccountFactory()),
        new PaymentRequestValidator(),
        serviceLocator.resolve(VehicleFactory.class),
        new BatteryResponseAssembler());
  }

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
      VehicleFactory vehicleFactory,
      BatteryResponseAssembler batteryResponseAssembler) {
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
    this.batteryResponseAssembler = batteryResponseAssembler;
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
      TransactionId transactionId,
      VehicleRequest vehicleRequest) {
    vehicleRequestValidator.validate(vehicleRequest);
    validateTransactionOwnership(containerRequestContext, transactionId);

    transactionService.addVehicle(
        transactionId, vehicleFactory.create(vehicleRequest.getModel(), vehicleRequest.getColor()));

    return Response.accepted().build();
  }

  @Override
  public Response addBattery(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      BatteryRequest batteryRequest) {
    batteryRequestValidator.validate(batteryRequest);
    validateTransactionOwnership(containerRequestContext, transactionId);

    transactionService.addBattery(transactionId, batteryRequest.getType());

    BigDecimal estimatedRange = transactionService.getVehicleEstimatedRange(transactionId);
    BatteryResponse batteryResponse = batteryResponseAssembler.assemble(estimatedRange);

    return Response.accepted().entity(batteryResponse).build();
  }

  @Override
  public Response completeTransaction(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      PaymentRequest paymentRequest) {
    paymentRequestValidator.validate(paymentRequest);
    validateTransactionOwnership(containerRequestContext, transactionId);

    Payment payment = paymentRequestAssembler.create(paymentRequest);
    transactionService.addPayment(transactionId, payment);

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
