package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTransactionRepository implements TransactionRepository {

  private final Map<TransactionId, Transaction> transactions = new HashMap<>();

  @Override
  public Transaction find(TransactionId transactionId) {
    if (transactions.containsKey(transactionId)) {
      return transactions.get(transactionId);
    }
    throw new TransactionNotFoundException(transactionId);
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
