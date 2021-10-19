package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class EmailContentTest {

  @Test
  public void givenValues_whenFormatBodyMessage_thenBodyMessageIsFormatted() {
    // given
    String aString = "doaskdsoa";
    String anotherString = "221321";
    String originalBodyMessage = "soadkasd %s adoksd %s";
    String expectedBodyMessage = "soadkasd doaskdsoa adoksd 221321";
    EmailContent emailContent = new EmailContent("a subject", originalBodyMessage);

    // when
    emailContent.formatBodyMessage(aString, anotherString);

    // then
    assertThat(emailContent.getBodyMessage()).matches(expectedBodyMessage);
  }

  @Test
  public void givenValues_whenFormatSubject_thenBodyMessageIsFormatted() {
    // given
    String aString = "doaskdsoa";
    String anotherString = "221321";
    String originalSubject = "soadkasd %s adoksd %s";
    String expectedSubject = "soadkasd doaskdsoa adoksd 221321";
    EmailContent emailContent = new EmailContent(originalSubject, "a body message");

    // when
    emailContent.formatSubject(aString, anotherString);

    // then
    assertThat(emailContent.getSubject()).matches(expectedSubject);
  }
}
