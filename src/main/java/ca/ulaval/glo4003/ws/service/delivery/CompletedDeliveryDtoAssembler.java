package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;

public class CompletedDeliveryDtoAssembler {

  public CompletedDeliveryDto assemble(Receipt receipt) {
    CompletedDeliveryDto completedDeliveryResponse = new CompletedDeliveryDto();
    completedDeliveryResponse.paymentTaken = receipt.getAmountPerPeriod().toInt();
    completedDeliveryResponse.paymentsLeft = receipt.getPaymentsLeft();
    return completedDeliveryResponse;
  }
}
