package ca.ulaval.glo4003.ws.domain.transaction.log;

import java.time.LocalDate;
import java.util.Collection;

public interface TransactionLogFinder {
  Collection<TransactionLogEntry> findAllForDate(LocalDate date);
}
