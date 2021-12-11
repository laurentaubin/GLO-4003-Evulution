package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;

public class ConfigurePaymentDtoAssembler {
  public ConfigurePaymentDto assemble(ConfigurePaymentRequest paymentRequest) {
    return new ConfigurePaymentDto(
        paymentRequest.getBankNumber(),
        paymentRequest.getAccountNumber(),
        paymentRequest.getFrequency());
  }
}
