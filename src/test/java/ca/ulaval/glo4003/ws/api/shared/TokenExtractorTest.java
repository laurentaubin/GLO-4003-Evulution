package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.shared.exception.EmptyTokenHeaderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenExtractorTest {
  private static final String A_AUTH_HEADER_NAME = "Bearer";
  private static final String A_AUTH_TOKEN = "some_token_value";
  private static final String A_AUTH_HEADER = A_AUTH_HEADER_NAME + " " + A_AUTH_TOKEN;
  private static final String ABSENT_AUTH_HEADER = null;

  @Test
  public void whenExtract_thenTokenIsExtractedFromHeader() {
    // given
    TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME);

    // when
    String extractedToken = tokenExtractor.extract(A_AUTH_HEADER);

    // then
    assertThat(extractedToken).isEqualTo(A_AUTH_TOKEN);
  }

  @Test
  public void givenAbsentHeader_whenExtract_thenTokenThrowEmptyHeaderException() {
    // given
    TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME);

    // when
    Executable execution = () -> tokenExtractor.extract(ABSENT_AUTH_HEADER);

    // then
    assertThrows(EmptyTokenHeaderException.class, execution);
  }
}
