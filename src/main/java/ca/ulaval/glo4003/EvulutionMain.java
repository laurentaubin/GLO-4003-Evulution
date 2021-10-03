package ca.ulaval.glo4003;

import ca.ulaval.glo4003.ws.api.delivery.DeliveryDestinationAssembler;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResource;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResourceImpl;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.filter.secured.AuthenticationFilter;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.mapper.*;
import ca.ulaval.glo4003.ws.api.transaction.CreatedTransactionResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.PaymentRequestAssembler;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResource;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResourceImpl;
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
import ca.ulaval.glo4003.ws.context.ApiContext;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.user.*;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;
import ca.ulaval.glo4003.ws.http.CorsResponseFilter;
import ca.ulaval.glo4003.ws.infrastructure.InMemoryModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.ModelDto;
import ca.ulaval.glo4003.ws.infrastructure.ModelDtoAssembler;
import ca.ulaval.glo4003.ws.infrastructure.auth.InMemorySessionRepository;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDto;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDtoAssembler;
import ca.ulaval.glo4003.ws.infrastructure.battery.InMemoryBatteryRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.InMemoryTransactionRepository;
import ca.ulaval.glo4003.ws.infrastructure.user.InMemoryUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import java.io.File;
import java.io.IOException;
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
  public static final String BASE_URI = "http://localhost";
  private static final String DEFAULT_PORT = "8080";
  private static final String PORT_JAVA_OPTION = "port";

  private static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";
  private static final String AUTHENTICATION_HEADER_NAME = "Bearer";

  private static final File BATTERY_INFO_FILE = new File("./target/classes/batteries.json");
  private static final File MODEL_INVENTORY = new File("./target/classes/models.json");

  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) throws Exception {
    new ApiContext().applyContext();

    UserRepository userRepository = new InMemoryUserRepository();
    SessionRepository sessionRepository = new InMemorySessionRepository();
    SessionTokenGenerator sessionTokenGenerator = new SessionTokenGenerator();
    SessionAdministrator sessionAdministrator =
        new SessionAdministrator(
            userRepository, sessionRepository, new SessionFactory(sessionTokenGenerator));
    TokenExtractor tokenExtractor = new TokenExtractor(AUTHENTICATION_HEADER_NAME);

    // Setup resources (API)
    RoleHandler roleHandler =
        new RoleHandler(userRepository, sessionRepository, sessionTokenGenerator, tokenExtractor);

    DeliveryOwnershipHandler deliveryOwnershipHandler =
        new DeliveryOwnershipHandler(userRepository);
    DeliveryResource deliveryResource =
        createDeliveryResource(roleHandler, deliveryOwnershipHandler);

    TransactionOwnershipHandler transactionOwnershipHandler =
        new TransactionOwnershipHandler(userRepository);
    TransactionResource transactionResource =
        createSalesResource(roleHandler, transactionOwnershipHandler, deliveryOwnershipHandler);

    UserResource userResource =
        createUserResource(userRepository, sessionRepository, sessionAdministrator);

    final AbstractBinder binder =
        new AbstractBinder() {
          @Override
          protected void configure() {
            bind(userResource).to(UserResource.class);
            bind(transactionResource).to(TransactionResource.class);
            bind(deliveryResource).to(DeliveryResource.class);
          }
        };

    final ResourceConfig config = new ResourceConfig();
    config.register(binder);
    config.register(new CorsResponseFilter());
    config.register(new CatchInvalidRequestFormatMapper());
    config.register(new CatchLoginFailedMapper());
    config.register(new CatchEmailAlreadyInUseExceptionMapper());
    config.register(new CatchSessionDoesNotExistExceptionMapper());
    config.register(new CatchUserNotFoundExceptionMapper());
    config.register(new CatchUnauthorizedUserExceptionMapper());
    config.register(new CatchEmptyTokenHeaderExceptionMapper());
    config.register(new CatchInvalidLocationExceptionMapper());
    config.register(new CatchBirthDateInTheFutureExceptionMapper());
    config.register(new CatchCannotAddBatteryBeforeVehicleExceptionMapper());

    config.register(
        new AuthenticationFilter(
            AUTHENTICATION_HEADER_NAME,
            sessionAdministrator,
            sessionTokenGenerator,
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

  private static TransactionResource createSalesResource(
      RoleHandler roleHandler,
      TransactionOwnershipHandler transactionOwnershipHandler,
      DeliveryOwnershipHandler deliveryOwnershipHandler)
      throws IOException {
    // Setup resources' dependencies (DOMAIN + INFRASTRUCTURE)
    TransactionRepository transactionRepository = new InMemoryTransactionRepository();
    BankAccountFactory bankAccountFactory = new BankAccountFactory();
    PaymentRequestAssembler paymentRequestAssembler =
        new PaymentRequestAssembler(bankAccountFactory);
    CreatedTransactionResponseAssembler createdTransactionResponseAssembler =
        new CreatedTransactionResponseAssembler();
    TransactionFactory transactionFactory = new TransactionFactory();

    VehicleRequestValidator vehicleRequestValidator =
        new VehicleRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());
    PaymentRequestValidator paymentRequestValidator =
        new PaymentRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());

    BatteryRequestValidator batteryRequestValidator =
        new BatteryRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());
    BatteryDtoAssembler batteryDTOAssembler = new BatteryDtoAssembler();
    ModelDtoAssembler modelDTOAssembler = new ModelDtoAssembler();

    BatteryRepository batteryRepository = setupBatteryInventory(batteryDTOAssembler);
    ModelRepository modelRepository = setUpModelInventory(modelDTOAssembler);

    TransactionService transactionService =
        new TransactionService(
            transactionRepository, transactionFactory, batteryRepository, modelRepository);

    return new TransactionResourceImpl(
        transactionService,
        ServiceLocator.getInstance().resolve(DeliveryService.class),
        transactionOwnershipHandler,
        deliveryOwnershipHandler,
        createdTransactionResponseAssembler,
        vehicleRequestValidator,
        roleHandler,
        batteryRequestValidator,
        paymentRequestAssembler,
        paymentRequestValidator);
  }

  private static DeliveryResource createDeliveryResource(
      RoleHandler roleHandler, DeliveryOwnershipHandler deliveryOwnershipHandler) {
    // Setup resources' dependencies (DOMAIN + INFRASTRUCTURE)

    DeliveryDestinationAssembler deliveryDestinationAssembler = new DeliveryDestinationAssembler();

    DeliveryService deliveryService = ServiceLocator.getInstance().resolve(DeliveryService.class);
    DeliveryRequestValidator deliveryRequestValidator =
        new DeliveryRequestValidator(Validation.buildDefaultValidatorFactory().getValidator());

    return new DeliveryResourceImpl(
        deliveryService,
        deliveryRequestValidator,
        deliveryDestinationAssembler,
        deliveryOwnershipHandler,
        roleHandler);
  }

  private static String getHttpPortFromArgs() {
    String httpPort = System.getProperty(PORT_JAVA_OPTION);

    if (httpPort == null) {
      httpPort = DEFAULT_PORT;
    }
    return httpPort;
  }

  // todo PUT in a .properties file ?
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

  private static BatteryRepository setupBatteryInventory(BatteryDtoAssembler batteryDTOAssembler)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Battery> batteriesListFromContext =
        batteryDTOAssembler.assembleBatteries(
            objectMapper.readValue(BATTERY_INFO_FILE, new TypeReference<List<BatteryDto>>() {}));
    Map<String, Battery> batteriesInventory = new HashMap<>();
    for (Battery battery : batteriesListFromContext) {
      batteriesInventory.put(battery.getType(), battery);
    }
    return new InMemoryBatteryRepository(batteriesInventory);
  }

  private static ModelRepository setUpModelInventory(ModelDtoAssembler modelDtoAssembler)
      throws IOException {
    ModelRepository modelRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    List<Model> modelListFromContext =
        modelDtoAssembler.assembleModels(
            objectMapper.readValue(MODEL_INVENTORY, new TypeReference<List<ModelDto>>() {}));
    Map<String, Model> modelInventory = new HashMap<>();
    for (Model model : modelListFromContext) {
      modelInventory.put(model.getName(), model);
    }
    modelRepository = new InMemoryModelRepository(modelInventory);
    return modelRepository;
  }
}
