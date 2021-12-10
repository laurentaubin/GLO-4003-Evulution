package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

public class DeliveryResourceImpl implements DeliveryResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final DeliveryService deliveryService;
  private final DeliveryRequestValidator deliveryRequestValidator;
  private final DeliveryDtoAssembler deliveryDtoAssembler;
  private final TokenExtractor tokenExtractor;

  public DeliveryResourceImpl() {
    this(
        serviceLocator.resolve(DeliveryService.class),
        new DeliveryRequestValidator(),
        new DeliveryDtoAssembler(),
            serviceLocator.resolve(TokenExtractor.class));
  }

  public DeliveryResourceImpl(
      DeliveryService deliveryService,
      DeliveryRequestValidator deliveryRequestValidator,
      DeliveryDtoAssembler deliveryDtoAssembler,
      TokenExtractor tokenExtractor) {
    this.deliveryService = deliveryService;
    this.deliveryRequestValidator = deliveryRequestValidator;
    this.deliveryDtoAssembler = deliveryDtoAssembler;
    this.tokenExtractor = tokenExtractor;
  }

  @Override
  public Response addDeliveryLocation(
      ContainerRequestContext containerRequestContext,
      DeliveryId deliveryId,
      DeliveryLocationRequest deliveryLocationRequest) {
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);
    deliveryRequestValidator.validate(deliveryLocationRequest);

    DeliveryLocationDto deliveryDto = deliveryDtoAssembler.assemble(deliveryLocationRequest);
    deliveryService.addDeliveryLocation(tokenDto, deliveryId, deliveryDto);
    return Response.accepted().build();
  }

  @Override
  public Response completeDelivery(
      ContainerRequestContext containerRequestContext, DeliveryId deliveryId) {
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);
    CompletedDeliveryDto deliveryResponseDto = deliveryService.completeDelivery(tokenDto, deliveryId);
    CompletedDeliveryResponse deliveryResponse = deliveryDtoAssembler.assemble(deliveryResponseDto);

    return Response.ok().entity(deliveryResponse).build();
  }
}
