package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TransactionRepositoryInMemory implements TransactionRepository {

  private Map<TransactionId, Transaction> transactions = new HashMap<>();

  @Override
  public Optional<Transaction> getTransaction(TransactionId transactionId) {
    return Optional.ofNullable(transactions.get(transactionId));
  }

  @Override
  public void save(Transaction transaction) {
    if (transactions.containsKey(transaction.getId())) {
      throw new DuplicateTransactionException(transaction.getId());
    }
    transactions.put(transaction.getId(), transaction);
  }

  @Override
  public void update(Transaction transaction) {
    Transaction foundTransaction = transactions.get(transaction.getId());
    if (foundTransaction != null) {
      transactions.put(transaction.getId(), transaction);
    } else {
      throw new TransactionNotFoundException(transaction.getId());
    }
  }
}
