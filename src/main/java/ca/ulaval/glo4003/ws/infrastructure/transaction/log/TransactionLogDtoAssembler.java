package ca.ulaval.glo4003.ws.infrastructure.transaction.log;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;

import java.math.BigDecimal;

public class TransactionLogDtoAssembler {

  public TransactionLogDto assemble(TransactionLogEntry transactionLogEntry) {
    return new TransactionLogDto(
        transactionLogEntry.getCreationDate(),
        BigDecimal.valueOf(transactionLogEntry.getTotalPrice().toDouble()),
        transactionLogEntry.getVehicleModel(),
        transactionLogEntry.getBatteryType());
  }

  public TransactionLogEntry assemble(TransactionLogDto transactionLogDto) {
    return new TransactionLogEntry(
        transactionLogDto.getCreationDate(),
        new Price(transactionLogDto.getTotalPrice()),
        transactionLogDto.getModelName(),
        transactionLogDto.getBatteryType());
  }
}
