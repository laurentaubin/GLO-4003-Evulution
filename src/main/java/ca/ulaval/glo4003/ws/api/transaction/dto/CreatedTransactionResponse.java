package ca.ulaval.glo4003.ws.api.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatedTransactionResponse {

  @JsonProperty("transaction_id")
  public String transactionId;
}
