package ca.ulaval.glo4003;

import ca.ulaval.glo4003.ws.context.ApiContext;
import ca.ulaval.glo4003.ws.context.ApplicationBinder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/** RESTApi setup without using DI or spring */
@SuppressWarnings("all")
public class EvulutionMain {
  public static boolean isDev = true; // Would be a JVM argument or in a .property file
  public static final String BASE_URI = "http://localhost";
  private static final String DEFAULT_PORT = "8080";
  private static final String PORT_JAVA_OPTION = "port";
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) throws Exception {
    new ApiContext().applyContext();

    final ResourceConfig config = new ResourceConfig();
    config.register(new ApplicationBinder());
    config.packages("ca.ulaval.glo4003.ws.api");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    String port = getHttpPortFromArgs();

    try {
      // Setup http server
      final Server server =
          JettyHttpContainerFactory.createServer(URI.create(BASE_URI + ":" + port), config);

      Runtime.getRuntime()
          .addShutdownHook(
              new Thread(
                  () -> {
                    try {
                      LOGGER.info("Shutting down the application...");
                      server.stop();
                      LOGGER.info("Done, exit.");

                    } catch (Exception e) {
                      LOGGER.info("Failed shutddown hook", e);
                    }
                  }));

      LOGGER.info("Application started.\nStop the application using CTRL+C");

      // block and wait shut down signal, like CTRL+C
      Thread.currentThread().join();
    } catch (InterruptedException e) {
      LOGGER.info("Thread was interrupted without joining properly", e);
    }
  }

  private static String getHttpPortFromArgs() {
    String httpPort = System.getProperty(PORT_JAVA_OPTION);

    if (httpPort == null || httpPort.isEmpty()) {
      httpPort = DEFAULT_PORT;
    }
    return httpPort;
  }
}
