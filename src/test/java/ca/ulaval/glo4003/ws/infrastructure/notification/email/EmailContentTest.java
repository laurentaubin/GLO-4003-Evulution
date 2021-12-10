package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

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
  public void givenValues_whenFormatSubject_thenSubjectIsFormatted() {
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

  @Test
  public void
      givenBodyMessageAlreadyFormatted_whenFormatBodyMessageAgain_thenBodyMessageIsFormatted() {
    // given
    String originalBodyMessage = "dasidiasj %s";
    EmailContent emailContent = new EmailContent("a subject", originalBodyMessage);
    emailContent.formatBodyMessage(111);
    String expectedBodyMessage = "dasidiasj 123";

    // when
    emailContent.formatBodyMessage(123);

    // then
    assertThat(emailContent.getBodyMessage()).matches(expectedBodyMessage);
  }

  @Test
  public void givenSubjectAlreadyFormatted_whenFormatSubjectAgain_thenSubjectIsFormatted() {
    // given
    String originalSubject = "dasidiasj %s";
    EmailContent emailContent = new EmailContent(originalSubject, "a body message");
    emailContent.formatSubject(111);
    String expectedSubject = "dasidiasj 123";

    // when
    emailContent.formatSubject(123);

    // then
    assertThat(emailContent.getSubject()).matches(expectedSubject);
  }
}
