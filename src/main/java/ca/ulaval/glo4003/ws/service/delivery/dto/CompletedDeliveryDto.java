package ca.ulaval.glo4003.ws.service.delivery.dto;

public class CompletedDeliveryDto {

  private final int paymentTaken;
  private final int paymentsLeft;

  public CompletedDeliveryDto(int paymentTaken, int paymentsLeft) {
    this.paymentTaken = paymentTaken;
    this.paymentsLeft = paymentsLeft;
  }

  public int getPaymentTaken() {
    return paymentTaken;
  }

  public int getPaymentsLeft() {
    return paymentsLeft;
  }
}
