package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;

public class CompletedDeliveryResponseAssembler {

  CompletedDeliveryResponse assemble(Receipt receipt) {
    CompletedDeliveryResponse completedDeliveryResponse = new CompletedDeliveryResponse();
    completedDeliveryResponse.paymentTaken = receipt.getAmountPerPeriod().toInt();
    completedDeliveryResponse.paymentsLeft = receipt.getPaymentsLeft();
    return completedDeliveryResponse;
  }
}
