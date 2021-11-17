package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;

public class CompletedDeliveryResponseAssembler {
  CompletedDeliveryResponse assemble(Integer paymentTaken, Integer paymentsLeft) {
    CompletedDeliveryResponse completedDeliveryResponse = new CompletedDeliveryResponse();
    completedDeliveryResponse.paymentTaken = paymentTaken;
    completedDeliveryResponse.paymentsLeft = paymentsLeft;
    return completedDeliveryResponse;
  }
}
