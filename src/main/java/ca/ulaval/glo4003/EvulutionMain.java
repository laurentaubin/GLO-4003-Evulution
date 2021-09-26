package ca.ulaval.glo4003;

import ca.ulaval.glo4003.ws.api.customer.CustomerAssembler;
import ca.ulaval.glo4003.ws.api.customer.CustomerResource;
import ca.ulaval.glo4003.ws.api.customer.CustomerResourceImpl;
import ca.ulaval.glo4003.ws.api.customer.LoginResponseAssembler;
import ca.ulaval.glo4003.ws.api.customer.validator.DateFormatValidator;
import ca.ulaval.glo4003.ws.api.customer.validator.RegisterCustomerDtoValidator;
import ca.ulaval.glo4003.ws.api.filters.AuthenticationFilter;
import ca.ulaval.glo4003.ws.api.mappers.CatchEmailAlreadyInUseExceptionMapper;
import ca.ulaval.glo4003.ws.api.mappers.CatchInvalidRequestFormatMapper;
import ca.ulaval.glo4003.ws.api.mappers.CatchLoginFailedMapper;
import ca.ulaval.glo4003.ws.api.mappers.InvalidRequestExceptionMapper;
import ca.ulaval.glo4003.ws.api.transaction.CreatedTransactionResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResource;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResourceImpl;
import ca.ulaval.glo4003.ws.api.transaction.VehicleRequestAssembler;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.api.util.DateParser;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenFactory;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenRepository;
import ca.ulaval.glo4003.ws.domain.customer.CustomerRepository;
import ca.ulaval.glo4003.ws.domain.customer.CustomerService;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionHandler;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.http.CorsResponseFilter;
import ca.ulaval.glo4003.ws.infrastructure.authnz.InMemoryLoginTokenRepository;
import ca.ulaval.glo4003.ws.infrastructure.customer.InMemoryCustomerRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.TransactionRepositoryInMemory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/** RESTApi setup without using DI or spring */
@SuppressWarnings("all")
public class EvulutionMain {
  public static boolean isDev = true; // Would be a JVM argument or in a .property file
  public static final String BASE_URI = "http://localhost:8080/";
  private static final String DEFAULT_PORT = "8080";
  private static final String PORT_JAVA_OPTION = "port";

  private static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";
  private static final String AUTHENTICATION_HEADER_NAME = "Bearer";

  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) throws Exception {

    CustomerRepository customerRepository = new InMemoryCustomerRepository();
    LoginTokenRepository loginTokenRepository = new InMemoryLoginTokenRepository();
    LoginTokenAdministrator loginTokenAdministrator =
        new LoginTokenAdministrator(
            customerRepository, loginTokenRepository, new LoginTokenFactory());

    // Setup resources (API)
    CustomerResource customerResource =
        createCustomerResource(customerRepository, loginTokenRepository, loginTokenAdministrator);
    TransactionResource transactionResource = createSalesResource();

    final AbstractBinder binder =
        new AbstractBinder() {
          @Override
          protected void configure() {
            bind(transactionResource).to(TransactionResource.class);
            bind(customerResource).to(CustomerResource.class);
          }
        };

    final ResourceConfig config = new ResourceConfig();
    config.register(binder);
    config.register(new CorsResponseFilter());
    config.register(new CatchInvalidRequestFormatMapper());
    config.register(new CatchLoginFailedMapper());
    config.register(new CatchEmailAlreadyInUseExceptionMapper());
    config.register(new InvalidRequestExceptionMapper());

    config.register(
        new AuthenticationFilter(
            AUTHENTICATION_HEADER_NAME, new LoginTokenFactory(), loginTokenAdministrator));
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

  private static TransactionResource createSalesResource() {
    // Setup resources' dependencies (DOMAIN + INFRASTRUCTURE)
    TransactionRepository transactionRepository = new TransactionRepositoryInMemory();
    VehicleRequestAssembler vehicleRequestAssembler = new VehicleRequestAssembler();
    CreatedTransactionResponseAssembler createdTransactionResponseAssembler =
        new CreatedTransactionResponseAssembler();
    TransactionHandler transactionHandler = new TransactionHandler();
    TransactionService transactionService =
        new TransactionService(transactionRepository, transactionHandler);
    VehicleRequestValidator vehicleRequestValidator =
        new VehicleRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());

    return new TransactionResourceImpl(
        transactionService,
        createdTransactionResponseAssembler,
        vehicleRequestAssembler,
        vehicleRequestValidator);
  }

  private static CustomerResource createCustomerResource(
      CustomerRepository customerRepository,
      LoginTokenRepository loginTokenRepository,
      LoginTokenAdministrator loginTokenAdministrator) {
    var customerService = new CustomerService(customerRepository, loginTokenAdministrator);
    var dateParser = new DateParser(DateTimeFormatter.ofPattern(BIRTH_DATE_PATTERN));
    var customerAssembler = new CustomerAssembler(dateParser);
    var dateFormatValidator = new DateFormatValidator(BIRTH_DATE_PATTERN);
    var defaultValidator = Validation.buildDefaultValidatorFactory().getValidator();
    var registerCustomerDtoValidator =
        new RegisterCustomerDtoValidator(defaultValidator, dateFormatValidator);

    return new CustomerResourceImpl(
        customerAssembler,
        new LoginResponseAssembler(),
        customerService,
        registerCustomerDtoValidator);
  }

  private static String getHttpPortFromArgs() {
    String httpPort = System.getProperty(PORT_JAVA_OPTION);

    if (httpPort == null) {
      httpPort = DEFAULT_PORT;
    }
    return httpPort;
  }
}
