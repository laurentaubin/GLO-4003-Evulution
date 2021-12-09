package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;

public class CompletedDeliveryDtoAssembler {

  public CompletedDeliveryDto assemble(Receipt receipt) {
    return new CompletedDeliveryDto(
        receipt.getAmountPerPeriod().toInt(), receipt.getPaymentsLeft());
  }
}
