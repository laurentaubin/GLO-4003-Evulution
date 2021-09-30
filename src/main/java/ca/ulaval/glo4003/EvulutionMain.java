package ca.ulaval.glo4003;

import ca.ulaval.glo4003.ws.api.filters.secured.AuthenticationFilter;
import ca.ulaval.glo4003.ws.api.mappers.*;
import ca.ulaval.glo4003.ws.api.transaction.*;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.api.user.LoginResponseAssembler;
import ca.ulaval.glo4003.ws.api.user.UserAssembler;
import ca.ulaval.glo4003.ws.api.user.UserResource;
import ca.ulaval.glo4003.ws.api.user.UserResourceImpl;
import ca.ulaval.glo4003.ws.api.user.validator.BirthDateValidator;
import ca.ulaval.glo4003.ws.api.user.validator.RegisterUserDtoValidator;
import ca.ulaval.glo4003.ws.api.util.DateParser;
import ca.ulaval.glo4003.ws.api.util.LocalDateProvider;
import ca.ulaval.glo4003.ws.api.util.TokenExtractor;
import ca.ulaval.glo4003.ws.api.validator.RoleValidator;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.transaction.BankAccountFactory;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionHandler;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.user.*;
import ca.ulaval.glo4003.ws.http.CorsResponseFilter;
import ca.ulaval.glo4003.ws.infrastructure.BatteryDTO;
import ca.ulaval.glo4003.ws.infrastructure.BatteryDTOAssembler;
import ca.ulaval.glo4003.ws.infrastructure.BatteryRepositoryInMemory;
import ca.ulaval.glo4003.ws.infrastructure.authnz.InMemorySessionRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.TransactionRepositoryInMemory;
import ca.ulaval.glo4003.ws.infrastructure.user.InMemoryUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private static final File BATTERY_INFO_FILE = new File("./src/main/resources/batteries.json");

  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) throws Exception {

    UserRepository userRepository = new InMemoryUserRepository();
    SessionRepository sessionRepository = new InMemorySessionRepository();
    SessionAdministrator sessionAdministrator =
        new SessionAdministrator(userRepository, sessionRepository, new SessionFactory());
    TokenExtractor tokenExtractor = new TokenExtractor(AUTHENTICATION_HEADER_NAME);

    // Setup resources (API)
    RoleValidator roleValidator =
        new RoleValidator(userRepository, sessionRepository, tokenExtractor);

    UserResource userResource =
        createUserResource(userRepository, sessionRepository, sessionAdministrator);

    TransactionResource transactionResource = createSalesResource(roleValidator);

    final AbstractBinder binder =
        new AbstractBinder() {
          @Override
          protected void configure() {
            bind(userResource).to(UserResource.class);
            bind(transactionResource).to(TransactionResource.class);
          }
        };

    final ResourceConfig config = new ResourceConfig();
    config.register(binder);
    config.register(new CorsResponseFilter());
    config.register(new CatchInvalidRequestFormatMapper());
    config.register(new CatchLoginFailedMapper());
    config.register(new CatchEmailAlreadyInUseExceptionMapper());
    config.register(new InvalidRequestExceptionMapper());
    config.register(new CatchSessionDoesNotExistExceptionMapper());
    config.register(new CatchUserNotFoundExceptionMapper());
    config.register(new CatchUnallowedUserExceptionMapper());
    config.register(new CatchEmptyTokenHeaderExceptionMapper());
    config.register(new CatchBirthDateInTheFutureExceptionMapper());

    config.register(
        new AuthenticationFilter(
            AUTHENTICATION_HEADER_NAME,
            new SessionFactory(),
            sessionAdministrator,
            tokenExtractor));
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

  private static UserResource createUserResource(
      UserRepository userRepository,
      SessionRepository sessionRepository,
      SessionAdministrator sessionAdministrator) {
    var userService = new UserService(userRepository, sessionAdministrator);
    createCatherinesAccount(userService);
    var dateParser = new DateParser(DateTimeFormatter.ofPattern(BIRTH_DATE_PATTERN));
    var userAssembler = new UserAssembler(dateParser);
    var dateFormatValidator = new BirthDateValidator(BIRTH_DATE_PATTERN, new LocalDateProvider());
    var defaultValidator = Validation.buildDefaultValidatorFactory().getValidator();
    var registerUserDtoValidator =
        new RegisterUserDtoValidator(defaultValidator, dateFormatValidator);

    return new UserResourceImpl(
        userAssembler, new LoginResponseAssembler(), userService, registerUserDtoValidator);
  }

  private static TransactionResource createSalesResource(RoleValidator roleValidator) {
    // Setup resources' dependencies (DOMAIN + INFRASTRUCTURE)
    TransactionRepository transactionRepository = new TransactionRepositoryInMemory();
    VehicleRequestAssembler vehicleRequestAssembler = new VehicleRequestAssembler();
    BankAccountFactory bankAccountFactory = new BankAccountFactory();
    PaymentRequestAssembler paymentRequestAssembler =
        new PaymentRequestAssembler(bankAccountFactory);
    CreatedTransactionResponseAssembler createdTransactionResponseAssembler =
        new CreatedTransactionResponseAssembler();
    TransactionHandler transactionHandler = new TransactionHandler();

    BatteryRepository batteryRepository = new BatteryRepositoryInMemory();

    TransactionService transactionService =
        new TransactionService(transactionRepository, transactionHandler, batteryRepository);
    VehicleRequestValidator vehicleRequestValidator =
        new VehicleRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());
    PaymentRequestValidator paymentRequestValidator =
        new PaymentRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());

    BatteryRequestValidator batteryRequestValidator =
        new BatteryRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());
    BatteryDTOAssembler batteryDTOAssembler = new BatteryDTOAssembler();

    setUpInventories(batteryRepository, batteryDTOAssembler);

    return new TransactionResourceImpl(
        transactionService,
        createdTransactionResponseAssembler,
        vehicleRequestAssembler,
        vehicleRequestValidator,
        roleValidator,
        batteryRequestValidator,
        paymentRequestAssembler,
        paymentRequestValidator);
  }

  private static String getHttpPortFromArgs() {
    String httpPort = System.getProperty(PORT_JAVA_OPTION);

    if (httpPort == null) {
      httpPort = DEFAULT_PORT;
    }
    return httpPort;
  }

  private static void createCatherinesAccount(UserService userService) {
    User adminUser =
        new User(
            "Catherine",
            new BirthDate(LocalDate.of(1997, 7, 31)),
            "F",
            "catherineleuf@evul.ulaval.ca",
            "RoulezVert2021!");
    adminUser.addRole(Role.ADMIN);
    userService.registerUser(adminUser);
  }

  // TODO WIP, rename and maybe move somewhere else this class is kinda packed + do the same for
  // cars
  private static void setUpInventories(
      BatteryRepository batteryRepository, BatteryDTOAssembler batteryDTOAssembler) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      List<Battery> batteriesListFromContext =
          batteryDTOAssembler.assembleBatteries(
              objectMapper.readValue(BATTERY_INFO_FILE, new TypeReference<List<BatteryDTO>>() {}));
      Map<String, Battery> batteriesInventory = new HashMap<>();
      for (Battery battery : batteriesListFromContext) {
        batteriesInventory.put(battery.getType(), battery);
      }
      batteryRepository.save(batteriesInventory);
    } catch (Exception e) {
      // TODO Map to the correct Error (500 error expected)
      System.out.println(e);
    }
  }
}
