package ca.ulaval.glo4003.ws.infrastructure.transaction.log;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogFinder;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogSink;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTransactionLogRepository implements TransactionLogSink, TransactionLogFinder {
  private final Map<LocalDate, Collection<TransactionLogDto>> transactionLogsByDate;
  private final TransactionLogDtoAssembler transactionLogDtoAssembler;

  public InMemoryTransactionLogRepository(TransactionLogDtoAssembler transactionLogDtoAssembler) {
    transactionLogsByDate = new HashMap<>();
    this.transactionLogDtoAssembler = transactionLogDtoAssembler;
  }

  @Override
  public Collection<TransactionLogEntry> findAllForDate(LocalDate date) {
    Collection<TransactionLogDto> transactionLogDtos = transactionLogsByDate.get(date);
    if (transactionLogDtos == null) {
      return new ArrayList<>();
    }

    return transactionLogDtos.stream()
        .map(transactionLogDtoAssembler::assemble)
        .collect(Collectors.toList());
  }

  @Override
  public void save(TransactionLogEntry transactionLogEntry) {
    Collection<TransactionLogDto> transactionLogDtos =
        transactionLogsByDate.get(transactionLogEntry.getCreationDate());

    if (transactionLogDtos == null) {
      transactionLogDtos = new ArrayList<>();
    }

    TransactionLogDto transactionLogDto = transactionLogDtoAssembler.assemble(transactionLogEntry);
    transactionLogDtos.add(transactionLogDto);
    transactionLogsByDate.put(transactionLogEntry.getCreationDate(), transactionLogDtos);
  }
}
