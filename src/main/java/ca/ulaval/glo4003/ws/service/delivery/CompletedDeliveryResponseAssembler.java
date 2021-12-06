package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryResponse;

public class CompletedDeliveryResponseAssembler {

  public CompletedDeliveryResponse assemble(Receipt receipt) {
    CompletedDeliveryResponse completedDeliveryResponse = new CompletedDeliveryResponse();
    completedDeliveryResponse.paymentTaken = receipt.getAmountPerPeriod().toInt();
    completedDeliveryResponse.paymentsLeft = receipt.getPaymentsLeft();
    return completedDeliveryResponse;
  }
}
