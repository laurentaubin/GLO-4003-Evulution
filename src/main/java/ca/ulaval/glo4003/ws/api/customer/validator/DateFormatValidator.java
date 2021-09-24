package ca.ulaval.glo4003.ws.api.customer.validator;

import ca.ulaval.glo4003.ws.api.customer.exception.InvalidFormatException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatValidator {
  private final String datePattern;
  private final DateTimeFormatter dateTimeFormatter;

  public DateFormatValidator(String datePattern) {
    this.datePattern = datePattern;
    dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
  }

  public void validateFormat(String date) {
    try {
      LocalDate.parse(date, dateTimeFormatter);
    } catch (DateTimeParseException e) {
      String description = String.format("birthdate does not follow the format %s", datePattern);
      throw new InvalidFormatException(description);
    }
  }
}
