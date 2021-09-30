package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryMode;
import ca.ulaval.glo4003.ws.domain.delivery.Location;

public class DeliveryDestinationAssembler {

  public DeliveryDestination assemble(DeliveryLocationRequest deliveryLocationRequest) {
    return new DeliveryDestination(
        DeliveryMode.fromString(deliveryLocationRequest.getMode()),
        Location.fromString(deliveryLocationRequest.getLocation()));
  }
}
