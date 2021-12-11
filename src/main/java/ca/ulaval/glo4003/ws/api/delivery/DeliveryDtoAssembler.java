package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.response.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.api.delivery.request.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;

public class DeliveryDtoAssembler {

  public DeliveryLocationDto assemble(DeliveryLocationRequest request) {
    return new DeliveryLocationDto(request.getMode(), request.getLocation());
  }

  public CompletedDeliveryResponse assemble(CompletedDeliveryDto response) {
    CompletedDeliveryResponse completedDeliveryResponse = new CompletedDeliveryResponse();
    completedDeliveryResponse.paymentsLeft = response.getPaymentsLeft();
    completedDeliveryResponse.paymentTaken = response.getPaymentTaken();
    return completedDeliveryResponse;
  }
}
