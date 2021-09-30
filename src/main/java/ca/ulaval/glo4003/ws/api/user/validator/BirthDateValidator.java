package ca.ulaval.glo4003.ws.api.user.validator;

import ca.ulaval.glo4003.ws.api.user.exception.BirthDateInTheFutureException;
import ca.ulaval.glo4003.ws.api.user.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.util.LocalDateProvider;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BirthDateValidator {
  private final String datePattern;
  private final DateTimeFormatter dateTimeFormatter;
  private final LocalDateProvider localDateProvider;

  public BirthDateValidator(String datePattern, LocalDateProvider localDateProvider) {
    this.datePattern = datePattern;
    dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
    this.localDateProvider = localDateProvider;
  }

  public void validate(String date) {
    LocalDate parsedDate = validateFormat(date);
    LocalDate todaysDate = localDateProvider.today();

    if (parsedDate.isAfter(todaysDate)) {
      throw new BirthDateInTheFutureException();
    }
  }

  private LocalDate validateFormat(String date) {
    try {
      return LocalDate.parse(date, dateTimeFormatter);
    } catch (DateTimeParseException e) {
      String description = String.format("birthdate does not follow the format %s", datePattern);
      throw new InvalidFormatException(description);
    }
  }
}
