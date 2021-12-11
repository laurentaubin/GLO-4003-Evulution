package ca.ulaval.glo4003.ws.service.transaction.dto;

public class TransactionCreationDto {
  private final String transactionId;
  private final String deliveryId;

  public TransactionCreationDto(String transactionId, String deliveryId) {
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
