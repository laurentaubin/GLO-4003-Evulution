package ca.ulaval.glo4003.ws.domain.transaction.log;

public interface TransactionLogSink {

  void save(TransactionLogEntry transactionLogEntry);
}
