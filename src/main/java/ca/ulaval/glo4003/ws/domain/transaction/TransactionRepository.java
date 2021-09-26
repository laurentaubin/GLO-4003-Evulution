package ca.ulaval.glo4003.ws.domain.transaction;

import java.util.Optional;

public interface TransactionRepository {

  Optional<Transaction> getTransaction(TransactionId transactionId);

  void save(Transaction transaction);

  void update(Transaction transaction);
}
