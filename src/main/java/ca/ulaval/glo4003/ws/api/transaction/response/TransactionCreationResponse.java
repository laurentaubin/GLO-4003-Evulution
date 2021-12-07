package ca.ulaval.glo4003.ws.api.transaction.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionCreationResponse {

  @JsonProperty("transaction_id")
  public String transactionId;

  @JsonProperty("delivery_id")
  public String deliveryId;

  public TransactionCreationResponse() {}

  public TransactionCreationResponse(String transactionId, String deliveryId) {
    this.transactionId = transactionId;
    this.deliveryId = deliveryId;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public String getDeliveryId() {
    return deliveryId;
  }
}
