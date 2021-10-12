package ca.ulaval.glo4003.ws.infrastructure.notification.email;

public class Email {
  private final EmailServer emailServer;
  private final String senderAddress;
  private final String recipientAddress;
  private final EmailContentDto emailContentDto;

  public Email(
      EmailServer emailServer,
      String senderAddress,
      String recipientAddress,
      EmailContentDto emailContentDto) {
    this.emailServer = emailServer;
    this.senderAddress = senderAddress;
    this.recipientAddress = recipientAddress;
    this.emailContentDto = emailContentDto;
  }

  public void send() {
    emailServer.send(senderAddress, recipientAddress, emailContentDto);
  }

  public EmailServer getEmailServer() {
    return emailServer;
  }

  public String getSenderAddress() {
    return senderAddress;
  }

  public String getRecipientAddress() {
    return recipientAddress;
  }

  public EmailContentDto getEmailContentDto() {
    return emailContentDto;
  }
}
