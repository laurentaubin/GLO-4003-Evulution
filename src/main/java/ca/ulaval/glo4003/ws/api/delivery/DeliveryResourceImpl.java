package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import jakarta.ws.rs.core.Response;

public class DeliveryResourceImpl implements DeliveryResource {
  public static final String ADD_DELIVERY_MESSAGE = "Transaction complete";
  private final DeliveryService deliveryService;
  private final DeliveryRequestValidator deliveryRequestValidator;
  private final DeliveryDestinationAssembler deliveryDestinationAssembler;

  public DeliveryResourceImpl(
      DeliveryService deliveryService,
      DeliveryRequestValidator deliveryRequestValidator,
      DeliveryDestinationAssembler deliveryDestinationAssembler) {
    this.deliveryService = deliveryService;
    this.deliveryRequestValidator = deliveryRequestValidator;
    this.deliveryDestinationAssembler = deliveryDestinationAssembler;
  }

  @Override
  public Response addDeliveryLocation(
      String deliveryId, DeliveryLocationRequest deliveryLocationRequest) {
    deliveryRequestValidator.validate(deliveryLocationRequest);
    DeliveryDestination deliveryDestination =
        deliveryDestinationAssembler.assemble(deliveryLocationRequest);
    deliveryService.addDeliveryDestination(new DeliveryId(deliveryId), deliveryDestination);
    return Response.ok().entity(ADD_DELIVERY_MESSAGE).build();
  }
}
