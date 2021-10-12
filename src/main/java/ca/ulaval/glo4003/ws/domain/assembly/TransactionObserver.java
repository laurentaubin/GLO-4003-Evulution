package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public interface TransactionObserver {
  void listenToTransactionCompleted(Transaction transaction);
}
