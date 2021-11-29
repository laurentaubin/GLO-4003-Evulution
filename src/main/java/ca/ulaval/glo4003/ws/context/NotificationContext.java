package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.context.exception.CouldNotLoadPropertiesFileException;
import ca.ulaval.glo4003.ws.domain.notification.NotificationIssuer;
import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailContent;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailNotificationIssuer;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailServer;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.NotificationEmailFactory;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta.JakartaEmailServer;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta.MessageFactory;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta.TransportWrapper;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NotificationContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String NOTIFICATION_EMAIL_ADDRESS = "equipe4archi@gmail.com";

  @Override
  public void registerContext() {
    registerJakartaEmailServer();
    registerEmailNotificationSystem();
  }

  private void registerJakartaEmailServer() {
    Session emailNotificationSession = createEmailNotificationSession();
    serviceLocator.register(
        EmailServer.class,
        new JakartaEmailServer(
            new MessageFactory(emailNotificationSession)));
  }

  private void registerEmailNotificationSystem() {
    serviceLocator.register(NotificationEmailFactory.class,
        new NotificationEmailFactory(createEmailContents()));
    serviceLocator.register(
        NotificationIssuer.class,
        new EmailNotificationIssuer(NOTIFICATION_EMAIL_ADDRESS));
    serviceLocator.register(NotificationService.class, new NotificationService());
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

  private Map<NotificationType, EmailContent> createEmailContents() {
    String delaySubject = "Your Evulution order #%s was delayed";

    return new HashMap<>() {
      {
        put(
            NotificationType.MODEL_ASSEMBLY_DELAY,
            new EmailContent(
                delaySubject,
                "Hello %s, \n\rWe encountered a delay while assembling the model included in your order. Your order now has a total delay of %s weeks and is expected to be delivered on %s. \r\nWe're sorry for the inconvenience. \r\n\r\nThanks for shopping with us, \r\nThe Evulution team"));
        put(
            NotificationType.BATTERY_ASSEMBLY_DELAY,
            new EmailContent(
                delaySubject,
                "Hello %s, \n\rWe encountered a delay while assembling the battery included in your order. Your order now has a total delay of %s weeks and is expected to be delivered on %s. \r\nWe're sorry for the inconvenience. \r\n\r\nThanks for shopping with us, \r\nThe Evulution team"));
        put(
            NotificationType.VEHICLE_ASSEMBLY_DELAY,
            new EmailContent(
                delaySubject,
                "Hello %s, \n\rWe encountered a delay while assembling the vehicle included in your order. Your order now has a total delay of %s weeks and is expected to be delivered on %s. \r\nWe're sorry for the inconvenience. \r\n\r\nThanks for shopping with us, \r\nThe Evulution team"));
        put(
            NotificationType.PRODUCTION_SHUTDOWN,
            new EmailContent(
                delaySubject,
                "Hello %s, \n\rWe encountered a delay while assembling the vehicle included in your order. The production line has shutdown and your order has been delayed for an undetermined amount of time -- we hope to resolve the issue in the most brief delay. \r\nWe're sorry for the inconvenience. \r\n\r\nThanks for shopping with us, \r\nThe Evulution team"));
      }
    };
  }
}
