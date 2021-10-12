package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.context.exception.CouldNotLoadPropertiesFileException;
import ca.ulaval.glo4003.ws.domain.notification.NotificationIssuer;
import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailContentDto;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailNotificationIssuer;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailServer;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.NotificationEmailFactory;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta.JakartaEmailServer;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta.MessageFactory;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta.TransportWrapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class NotificationContext implements Context {
  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String HOST = "smtp.gmail.com";
  private static final int PORT = 587;
  private static final String USER = "equipe4archi@gmail.com";
  private static final String PASSWORD = "Equipe4arch1!";
  private static final String NOTIFICATION_EMAIL_ADDRESS = "equipe4archi@gmail.com";

  @Override
  public void registerContext() {
    registerJakartaEmailServer();
    registerEmailNotificationSystem();
  }

  private void registerJakartaEmailServer() {
    Session emailNotificationSession = createEmailNotificationSession();
    serviceLocator.register(MessageFactory.class, new MessageFactory(emailNotificationSession));
    serviceLocator.register(TransportWrapper.class, new TransportWrapper());
    serviceLocator.register(
        EmailServer.class,
        new JakartaEmailServer(
            serviceLocator.resolve(MessageFactory.class),
            serviceLocator.resolve(TransportWrapper.class)));
  }

  private void registerEmailNotificationSystem() {
    Map<NotificationType, EmailContentDto> emailContents = createEmailContents();
    serviceLocator.register(
        NotificationEmailFactory.class,
        new NotificationEmailFactory(serviceLocator.resolve(EmailServer.class), emailContents));
    serviceLocator.register(
        NotificationIssuer.class,
        new EmailNotificationIssuer(
            NOTIFICATION_EMAIL_ADDRESS, serviceLocator.resolve(NotificationEmailFactory.class)));
  }

  private Session createEmailNotificationSession() {
    Properties properties = openEmailConfig();

    return Session.getInstance(
        properties,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(
                properties.getProperty("email.address"), properties.getProperty("email.password"));
          }
        });
  }

  private Properties openEmailConfig() {
    try {
      InputStream configFile = new FileInputStream("./target/classes/emailConfig.properties");

      Properties properties = new Properties();
      properties.load(configFile);
      return properties;

    } catch (IOException e) {
      e.printStackTrace();
      throw new CouldNotLoadPropertiesFileException(e);
    }
  }

  private Map<NotificationType, EmailContentDto> createEmailContents() {
    return new HashMap<>() {
      {
        put(
            NotificationType.ASSEMBLY_LINE_DELAY,
            new EmailContentDto(
                "There was a delay in your vehicle assembly",
                "TODO, a voir comment on veut formatter le message (surement avec des %s qu'on remplace par des valeurs passées par l'assembler)"));
      }
    };
  }
}
