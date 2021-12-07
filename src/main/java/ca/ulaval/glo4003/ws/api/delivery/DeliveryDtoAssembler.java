package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;

public class DeliveryDtoAssembler {

  public DeliveryLocationDto assemble(DeliveryLocationRequest request) {
    DeliveryLocationDto deliveryLocationRequestDto = new DeliveryLocationDto();
    deliveryLocationRequestDto.mode = request.getMode();
    deliveryLocationRequestDto.location = request.getLocation();
    return deliveryLocationRequestDto;
  }

  public CompletedDeliveryResponse assemble(CompletedDeliveryDto response) {
    CompletedDeliveryResponse completedDeliveryResponse = new CompletedDeliveryResponse();
    completedDeliveryResponse.paymentsLeft = response.paymentsLeft;
    completedDeliveryResponse.paymentTaken = response.paymentTaken;
    return completedDeliveryResponse;
  }
}
