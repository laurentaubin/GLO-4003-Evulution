package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.warehouse.TransactionCompletedObserver;

import java.util.ArrayList;
import java.util.List;

public class TransactionCompletedObservable {
  private final List<TransactionCompletedObserver> observers = new ArrayList<>();

  public void register(TransactionCompletedObserver observer) {
    observers.add(observer);
  }

  public void notifyTransactionCompleted(Transaction transaction) {
    for (TransactionCompletedObserver observer : observers) {
      observer.listenToTransactionCompleted(transaction);
    }
  }
}
