package ca.ulaval.glo4003.ws.service.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatedTransactionResponse {

  @JsonProperty("transaction_id")
  public String transactionId;

  @JsonProperty("delivery_id")
  public String deliveryId;
}
