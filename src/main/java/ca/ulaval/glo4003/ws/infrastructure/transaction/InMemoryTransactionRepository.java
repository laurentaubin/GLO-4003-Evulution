package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTransactionRepository implements TransactionRepository {

  private final Map<TransactionId, TransactionDto> transactions = new HashMap<>();
  private final TransactionAssembler transactionAssembler;

  public InMemoryTransactionRepository() {
    this(new TransactionAssembler());
  }

  public InMemoryTransactionRepository(TransactionAssembler transactionAssembler) {
    this.transactionAssembler = transactionAssembler;
  }

  @Override
  public Transaction find(TransactionId transactionId) {
    if (transactions.containsKey(transactionId)) {
      TransactionDto transactionDto = transactions.get(transactionId);
      return transactionAssembler.assemble(transactionDto);
    }
    throw new TransactionNotFoundException(transactionId);
  }

  @Override
  public void save(Transaction transaction) {
    if (transactions.containsKey(transaction.getId())) {
      throw new DuplicateTransactionException(transaction.getId());
    }
    TransactionDto transactionDto = transactionAssembler.assemble(transaction);
    transactions.put(transaction.getId(), transactionDto);
  }

  @Override
  public void update(Transaction transaction) {
    TransactionDto foundTransaction = transactions.get(transaction.getId());
    if (foundTransaction != null) {
      TransactionDto transactionDto = transactionAssembler.assemble(transaction);
      transactions.put(transaction.getId(), transactionDto);
    } else {
      throw new TransactionNotFoundException(transaction.getId());
    }
  }
}
