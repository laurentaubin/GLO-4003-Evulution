package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.Payment;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;

public class TransactionDto {
  private final TransactionId id;
  private final Vehicle vehicle;
  private final Payment payment;

  public TransactionDto(TransactionId id, Vehicle vehicle, Payment payment) {
    this.id = id;
    this.vehicle = vehicle;
    this.payment = payment;
  }

  public TransactionId getId() {
    return id;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public Payment getPayment() {
    return payment;
  }
}
