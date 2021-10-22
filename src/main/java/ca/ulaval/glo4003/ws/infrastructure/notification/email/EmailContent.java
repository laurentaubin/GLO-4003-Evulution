package ca.ulaval.glo4003.ws.infrastructure.notification.email;

public class EmailContent {
  private final String subjectTemplate;
  private final String bodyMessageTemplate;
  private String formattedSubject;
  private String formattedBodyMessage;

  public EmailContent(String subjectTemplate, String bodyMessageTemplate) {
    this.subjectTemplate = subjectTemplate;
    this.bodyMessageTemplate = bodyMessageTemplate;
    this.formattedSubject = subjectTemplate;
    this.formattedBodyMessage = bodyMessageTemplate;
  }

  public String getSubject() {
    return formattedSubject;
  }

  public String getBodyMessage() {
    return formattedBodyMessage;
  }

  public void formatSubject(Object... objects) {
    formattedSubject = String.format(subjectTemplate, objects);
  }

  public void formatBodyMessage(Object... objects) {
    formattedBodyMessage = String.format(bodyMessageTemplate, objects);
  }
}
