package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.response.BatteryConfigurationResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.response.TransactionCreationResponseAssembler;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryConfigurationDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransactionResourceImpl implements TransactionResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final List<Role> PRIVILEGED_ROLES =
      new ArrayList<>(List.of(Role.BASE, Role.PRODUCTION_MANAGER));

  private final TransactionService transactionService;
  private final AuthenticationService authenticationService;
  private final ConfigureVehicleDtoAssembler vehicleConfigurationDtoAssembler;
  private final ConfigureBatteryDtoAssembler batteryConfigurationDtoAssembler;
  private final BatteryConfigurationResponseAssembler configureBatteryResponseAssembler;
  private final ConfigurePaymentDtoAssembler configurePaymentDtoAssembler;
  private final RequestValidator requestValidator;
  private final TransactionCreationResponseAssembler transactionCreationResponseAssembler;

  public TransactionResourceImpl() {
    this(
        serviceLocator.resolve(TransactionService.class),
        serviceLocator.resolve(AuthenticationService.class),
        new ConfigureVehicleDtoAssembler(),
        new ConfigureBatteryDtoAssembler(),
        new BatteryConfigurationResponseAssembler(),
        new ConfigurePaymentDtoAssembler(),
        new RequestValidator(),
        new TransactionCreationResponseAssembler());
  }

  public TransactionResourceImpl(
      TransactionService transactionService,
      AuthenticationService authenticationService,
      ConfigureVehicleDtoAssembler vehicleConfigurationDtoAssembler,
      ConfigureBatteryDtoAssembler batteryConfigurationDtoAssembler,
      BatteryConfigurationResponseAssembler batteryConfigurationResponseAssembler,
      ConfigurePaymentDtoAssembler configurePaymentDtoAssembler,
      RequestValidator requestValidator,
      TransactionCreationResponseAssembler transactionCreationResponseAssembler) {
    this.transactionService = transactionService;
    this.authenticationService = authenticationService;
    this.vehicleConfigurationDtoAssembler = vehicleConfigurationDtoAssembler;
    this.batteryConfigurationDtoAssembler = batteryConfigurationDtoAssembler;
    this.configureBatteryResponseAssembler = batteryConfigurationResponseAssembler;
    this.configurePaymentDtoAssembler = configurePaymentDtoAssembler;
    this.requestValidator = requestValidator;
    this.transactionCreationResponseAssembler = transactionCreationResponseAssembler;
  }

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    Session userSession =
        authenticationService.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
    TransactionCreationDto transactionCreationDto = transactionService.createTransaction();
    authenticationService.mapDeliveryIdToTransactionId(
        userSession,
        transactionCreationDto.getTransactionId(),
        transactionCreationDto.getDeliveryId());

    URI transactionUri =
        URI.create(String.format("/sales/%s", transactionCreationDto.getTransactionId()));
    return Response.created(transactionUri)
        .entity(transactionCreationResponseAssembler.assemble(transactionCreationDto))
        .build();
  }

  @Override
  public Response configureVehicle(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      ConfigureVehicleRequest vehicleConfigurationRequest) {
    requestValidator.validate(vehicleConfigurationRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);
    ConfigureVehicleDto vehicleConfigurationDto =
        vehicleConfigurationDtoAssembler.assemble(vehicleConfigurationRequest);

    transactionService.configureVehicle(transactionId, vehicleConfigurationDto);

    return Response.accepted().build();
  }

  @Override
  public Response configureBattery(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      ConfigureBatteryRequest batteryConfigurationRequest) {
    requestValidator.validate(batteryConfigurationRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);
    ConfigureBatteryDto configureBatteryDto =
        batteryConfigurationDtoAssembler.assemble(batteryConfigurationRequest);

    BatteryConfigurationDto batteryConfigurationDto =
        transactionService.configureBattery(transactionId, configureBatteryDto);

    return Response.accepted()
        .entity(
            configureBatteryResponseAssembler.assemble(batteryConfigurationDto.getEstimatedRange()))
        .build();
  }

  @Override
  public Response completeTransaction(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      ConfigurePaymentRequest configurePaymentRequest) {
    requestValidator.validate(configurePaymentRequest);
    authenticationService.validateTransactionOwnership(
        containerRequestContext, transactionId, PRIVILEGED_ROLES);

    ConfigurePaymentDto configurePaymentDto =
        configurePaymentDtoAssembler.assemble(configurePaymentRequest);

    transactionService.completeTransaction(transactionId, configurePaymentDto);

    return Response.ok().build();
  }
}
