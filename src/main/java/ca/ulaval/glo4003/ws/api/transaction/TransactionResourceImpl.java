package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.response.BatteryConfigurationResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.response.TransactionCreationResponseAssembler;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.*;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.net.URI;

public class TransactionResourceImpl implements TransactionResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private final TransactionService transactionService;
  private final ConfigureVehicleDtoAssembler vehicleConfigurationDtoAssembler;
  private final ConfigureBatteryDtoAssembler batteryConfigurationDtoAssembler;
  private final BatteryConfigurationResponseAssembler configureBatteryResponseAssembler;
  private final ConfigurePaymentDtoAssembler configurePaymentDtoAssembler;
  private final RequestValidator requestValidator;
  private final TransactionCreationResponseAssembler transactionCreationResponseAssembler;
  private final TokenExtractor tokenExtractor;

  public TransactionResourceImpl() {
    this(
        serviceLocator.resolve(TransactionService.class),
        new ConfigureVehicleDtoAssembler(),
        new ConfigureBatteryDtoAssembler(),
        new BatteryConfigurationResponseAssembler(),
        new ConfigurePaymentDtoAssembler(),
        new RequestValidator(),
        new TransactionCreationResponseAssembler(),
            serviceLocator.resolve(TokenExtractor.class));
  }

  public TransactionResourceImpl(
      TransactionService transactionService,
      ConfigureVehicleDtoAssembler vehicleConfigurationDtoAssembler,
      ConfigureBatteryDtoAssembler batteryConfigurationDtoAssembler,
      BatteryConfigurationResponseAssembler batteryConfigurationResponseAssembler,
      ConfigurePaymentDtoAssembler configurePaymentDtoAssembler,
      RequestValidator requestValidator,
      TransactionCreationResponseAssembler transactionCreationResponseAssembler,
      TokenExtractor tokenExtractor) {
    this.transactionService = transactionService;
    this.vehicleConfigurationDtoAssembler = vehicleConfigurationDtoAssembler;
    this.batteryConfigurationDtoAssembler = batteryConfigurationDtoAssembler;
    this.configureBatteryResponseAssembler = batteryConfigurationResponseAssembler;
    this.configurePaymentDtoAssembler = configurePaymentDtoAssembler;
    this.requestValidator = requestValidator;
    this.transactionCreationResponseAssembler = transactionCreationResponseAssembler;
    this.tokenExtractor = tokenExtractor;
  }

  @Override
  public Response createTransaction(ContainerRequestContext containerRequestContext) {
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);

    TransactionCreationDto transactionCreationDto = transactionService.createTransaction(tokenDto);

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
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);

    requestValidator.validate(vehicleConfigurationRequest);
    ConfigureVehicleDto vehicleConfigurationDto =
        vehicleConfigurationDtoAssembler.assemble(vehicleConfigurationRequest);

    transactionService.configureVehicle(transactionId, vehicleConfigurationDto, tokenDto);

    return Response.accepted().build();
  }

  @Override
  public Response configureBattery(
      ContainerRequestContext containerRequestContext,
      TransactionId transactionId,
      ConfigureBatteryRequest batteryConfigurationRequest) {
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);
    requestValidator.validate(batteryConfigurationRequest);
    ConfigureBatteryDto configureBatteryDto =
        batteryConfigurationDtoAssembler.assemble(batteryConfigurationRequest);

    BatteryConfigurationDto batteryConfigurationDto =
        transactionService.configureBattery(transactionId, configureBatteryDto, tokenDto);

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
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);
    requestValidator.validate(configurePaymentRequest);

    ConfigurePaymentDto configurePaymentDto =
        configurePaymentDtoAssembler.assemble(configurePaymentRequest);

    transactionService.completeTransaction(transactionId, configurePaymentDto, tokenDto);

    return Response.ok().build();
  }
}
