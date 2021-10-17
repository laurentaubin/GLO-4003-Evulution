package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class TransactionAssembler {

  public TransactionDto assemble(Transaction transaction) {
    return new TransactionDto(
        transaction.getId(), transaction.getVehicle(), transaction.getPayment());
  }

  public Transaction assemble(TransactionDto transactionDto) {
    Transaction transaction = new Transaction(transactionDto.getId());

    if (transactionDto.getVehicle() != null) {
      transaction.addVehicle(transactionDto.getVehicle());

      if (transactionDto.getVehicle().hasBattery()) {
        transaction.addPayment(transactionDto.getPayment());
      }
    }

    return transaction;
  }
}
