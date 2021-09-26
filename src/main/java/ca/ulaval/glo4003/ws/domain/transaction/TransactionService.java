package ca.ulaval.glo4003.ws.domain.transaction;

public class TransactionService {

  private TransactionRepository transactionRepository;
  private TransactionHandler transactionHandler;

  public TransactionService(
      TransactionRepository transactionRepository, TransactionHandler transactionHandler) {
    this.transactionRepository = transactionRepository;
    this.transactionHandler = transactionHandler;
  }

  public Transaction createTransaction() {
    Transaction transaction = transactionHandler.createTransaction();
    transactionRepository.save(transaction);
    return transaction;
  }

  public void addVehicle(TransactionId transactionId, Vehicle vehicle) {
    Transaction transaction =
        transactionRepository
            .getTransaction(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    transactionHandler.setVehicle(transaction, vehicle);
    transactionRepository.update(transaction);
  }
}
