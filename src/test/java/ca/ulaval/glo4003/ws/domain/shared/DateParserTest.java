package ca.ulaval.glo4003.ws.domain.shared;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateParserTest {
  private static final String A_DATE_FORMAT = "yyyy-MM-dd";

  private DateParser dateParser;

  @BeforeEach
  public void setUp() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(A_DATE_FORMAT);

    dateParser = new DateParser(dateTimeFormatter);
  }

  @Test
  public void givenDateInTheRightFormat_whenParse_thenReturnExpectedDate() {
    // given
    String unparsedDate = "1888-06-07";
    LocalDate expectedDate = LocalDate.of(1888, 6, 7);

    // when
    LocalDate actualDate = dateParser.parse(unparsedDate);

    // then
    assertThat(actualDate).isEquivalentAccordingToCompareTo(expectedDate);
  }

  @Test
  public void givenDateInTheWrongFormat_whenParse_thenThrowDateTimeParseException() {
    // given
    String unparsedDate = "188-06-07";

    // when
    Executable parsingDate = () -> dateParser.parse(unparsedDate);

    // then
    assertThrows(DateTimeParseException.class, parsingDate);
  }
}
