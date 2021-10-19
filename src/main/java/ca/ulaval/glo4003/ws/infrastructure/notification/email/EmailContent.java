package ca.ulaval.glo4003.ws.infrastructure.notification.email;

public class EmailContent {
  private String subject;
  private String bodyMessage;

  public EmailContent(String subject, String bodyMessage) {
    this.subject = subject;
    this.bodyMessage = bodyMessage;
  }

  public String getSubject() {
    return subject;
  }

  public String getBodyMessage() {
    return bodyMessage;
  }

  public void formatSubject(Object... objects) {
    subject = String.format(subject, objects);
  }

  public void formatBodyMessage(Object... objects) {
    bodyMessage = String.format(bodyMessage, objects);
  }
}
