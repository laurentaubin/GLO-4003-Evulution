package ca.ulaval.glo4003.ws.api.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateParser {

  private final DateTimeFormatter dateTimeFormatter;

  public DateParser(DateTimeFormatter dateTimeFormatter) {
    this.dateTimeFormatter = dateTimeFormatter;
  }

  public LocalDate parse(String unparsedDate) {
    return LocalDate.parse(unparsedDate, dateTimeFormatter);
  }
}
