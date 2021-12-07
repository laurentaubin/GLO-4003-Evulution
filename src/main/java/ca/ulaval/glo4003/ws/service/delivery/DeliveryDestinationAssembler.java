package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryMode;
import ca.ulaval.glo4003.ws.domain.delivery.Location;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;

public class DeliveryDestinationAssembler {

  public DeliveryDestination assemble(DeliveryLocationDto deliveryLocationRequest) {
    return new DeliveryDestination(
        DeliveryMode.fromString(deliveryLocationRequest.mode),
        Location.fromString(deliveryLocationRequest.location));
  }
}
