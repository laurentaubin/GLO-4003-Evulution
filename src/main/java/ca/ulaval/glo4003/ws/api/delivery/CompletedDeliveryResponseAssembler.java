package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;

public class CompletedDeliveryResponseAssembler {
  CompletedDeliveryResponse assemble() {
    CompletedDeliveryResponse completedDeliveryResponse = new CompletedDeliveryResponse();
    completedDeliveryResponse.paymentTaken = 200;
    completedDeliveryResponse.paymentsLeft = 83;
    return completedDeliveryResponse;
  }
}
