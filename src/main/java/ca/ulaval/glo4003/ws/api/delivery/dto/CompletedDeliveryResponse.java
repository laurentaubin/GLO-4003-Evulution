package ca.ulaval.glo4003.ws.api.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletedDeliveryResponse {
  @JsonProperty("payment_taken")
  public int paymentTaken;

  @JsonProperty("number_of_payments_left")
  public int paymentsLeft;
}
