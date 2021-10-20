package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.filter.secured.AuthenticationFilter;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.shared.DateParser;
import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.api.user.LoginResponseAssembler;
import ca.ulaval.glo4003.ws.api.user.UserAssembler;
import ca.ulaval.glo4003.ws.api.user.UserResource;
import ca.ulaval.glo4003.ws.api.user.UserResourceImpl;
import ca.ulaval.glo4003.ws.api.user.validator.BirthDateValidator;
import ca.ulaval.glo4003.ws.api.user.validator.RegisterUserDtoValidator;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.*;
import ca.ulaval.glo4003.ws.infrastructure.auth.InMemorySessionRepository;
import ca.ulaval.glo4003.ws.infrastructure.user.InMemoryUserRepository;
import ca.ulaval.glo4003.ws.infrastructure.user.UserDtoAssembler;
import jakarta.validation.Validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserContext implements Context {
  private static final String AUTHENTICATION_HEADER_NAME = "Bearer";
  private static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";

  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerRepositories();
    registerSessionServices();
    registerFilters();
    registerUserServices();
    registerResources();
  }

  private void registerRepositories() {
    serviceLocator.register(UserDtoAssembler.class, new UserDtoAssembler());
    serviceLocator.register(
        UserRepository.class,
        new InMemoryUserRepository(serviceLocator.resolve(UserDtoAssembler.class)));
    serviceLocator.register(SessionRepository.class, new InMemorySessionRepository());
  }

  private void registerSessionServices() {
    serviceLocator.register(SessionTokenGenerator.class, new SessionTokenGenerator());
    serviceLocator.register(TokenExtractor.class, new TokenExtractor(AUTHENTICATION_HEADER_NAME));
    serviceLocator.register(
        SessionFactory.class,
        new SessionFactory(serviceLocator.resolve(SessionTokenGenerator.class)));

    serviceLocator.register(
        SessionAdministrator.class,
        new SessionAdministrator(
            serviceLocator.resolve(UserRepository.class),
            serviceLocator.resolve(SessionRepository.class),
            serviceLocator.resolve(SessionFactory.class)));

    serviceLocator.register(
        RoleHandler.class,
        new RoleHandler(
            serviceLocator.resolve(UserRepository.class),
            serviceLocator.resolve(SessionRepository.class),
            serviceLocator.resolve(SessionTokenGenerator.class),
            serviceLocator.resolve(TokenExtractor.class)));

    serviceLocator.register(
        DeliveryOwnershipHandler.class,
        new DeliveryOwnershipHandler(serviceLocator.resolve(UserRepository.class)));

    serviceLocator.register(
        TransactionOwnershipHandler.class,
        new TransactionOwnershipHandler(serviceLocator.resolve(UserRepository.class)));
  }

  private void registerFilters() {
    serviceLocator.register(
        AuthenticationFilter.class,
        new AuthenticationFilter(
            AUTHENTICATION_HEADER_NAME,
            serviceLocator.resolve(SessionAdministrator.class),
            serviceLocator.resolve(SessionTokenGenerator.class),
            serviceLocator.resolve(TokenExtractor.class)));
  }

  private void registerUserServices() {
    var dateParser = new DateParser(DateTimeFormatter.ofPattern(BIRTH_DATE_PATTERN));
    var defaultValidator = Validation.buildDefaultValidatorFactory().getValidator();

    serviceLocator.register(
        UserService.class,
        new UserService(
            serviceLocator.resolve(UserRepository.class),
            serviceLocator.resolve(SessionAdministrator.class)));

    serviceLocator.register(
        BirthDateValidator.class,
        new BirthDateValidator(BIRTH_DATE_PATTERN, new LocalDateProvider()));
    serviceLocator.register(UserAssembler.class, new UserAssembler(dateParser));
    serviceLocator.register(
        RegisterUserDtoValidator.class,
        new RegisterUserDtoValidator(
            defaultValidator, serviceLocator.resolve(BirthDateValidator.class)));
    serviceLocator.register(LoginResponseAssembler.class, new LoginResponseAssembler());
    createCatherinesAccount();
  }

  private void registerResources() {
    serviceLocator.register(
        UserResource.class,
        new UserResourceImpl(
            serviceLocator.resolve(UserAssembler.class),
            serviceLocator.resolve(LoginResponseAssembler.class),
            serviceLocator.resolve(UserService.class),
            serviceLocator.resolve(RegisterUserDtoValidator.class)));
  }

  private static void createCatherinesAccount() {
    User adminUser =
        new User(
            "Catherine",
            new BirthDate(LocalDate.of(1997, 7, 31)),
            "F",
            "catherineleuf@evul.ulaval.ca",
            "RoulezVert2021!");
    adminUser.addRole(Role.ADMIN);
    serviceLocator.resolve(UserService.class).registerUser(adminUser);
  }
}
