package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccount;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.Color;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;

import java.util.UUID;

public class TransactionFixture {
  TransactionBuilder transactionBuilder = new TransactionBuilder();

  public Transaction givenACompleteTransaction() {
    return transactionBuilder.build();
  }

  private class TransactionBuilder {
    private TransactionId transactionId = new TransactionId(UUID.randomUUID().toString());
    private Vehicle vehicle = new Vehicle(new ModelBuilder().build(), Color.WHITE);
    private Battery battery = new Battery("type", 100, 100, new Price(100), new ProductionTime(0));
    private Payment payment = new Payment(new BankAccount("123", "12345"), Frequency.MONTHLY);

    public Transaction build() {
      Transaction transaction = new Transaction(transactionId);
      transaction.addVehicle(vehicle);
      transaction.addBattery(battery);
      transaction.addPayment(payment);

      return transaction;
    }
  }
}
