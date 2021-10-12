package ca.ulaval.glo4003.ws.infrastructure.notification.email;

public class EmailContentDto {
  private final String subject;
  private final String bodyMessage;

  public EmailContentDto(String subject, String bodyMessage) {
    this.subject = subject;
    this.bodyMessage = bodyMessage;
  }

  public String getSubject() {
    return subject;
  }

  public String getBodyMessage() {
    return bodyMessage;
  }
}
