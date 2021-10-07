package ca.ulaval.glo4003.ws.domain.transaction;

public interface TransactionRepository {
  Transaction find(TransactionId transactionId);

  void save(Transaction transaction);

  void update(Transaction transaction);
}
