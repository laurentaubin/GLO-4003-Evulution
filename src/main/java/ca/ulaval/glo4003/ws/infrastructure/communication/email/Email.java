package ca.ulaval.glo4003.ws.infrastructure.communication.email;

public class Email {
  private final EmailServer emailServer;
  private final String senderAddress;
  private final String recipientAddress;
  private final EmailContent emailContent;

  public Email(
      EmailServer emailServer,
      String senderAddress,
      String recipientAddress,
      EmailContent emailContent) {
    this.emailServer = emailServer;
    this.senderAddress = senderAddress;
    this.recipientAddress = recipientAddress;
    this.emailContent = emailContent;
  }

  public void send() {
    emailServer.send(senderAddress, recipientAddress, emailContent);
  }

  public String getSenderAddress() {
    return senderAddress;
  }

  public String getRecipientAddress() {
    return recipientAddress;
  }
}
