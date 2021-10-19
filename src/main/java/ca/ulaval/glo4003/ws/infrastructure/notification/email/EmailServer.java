package ca.ulaval.glo4003.ws.infrastructure.notification.email;

public interface EmailServer {

  void send(String sender, String recipient, EmailContent emailContent);
}
