package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.api.transaction.TokenDtoAssembler;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateWrapper;
import ca.ulaval.glo4003.ws.domain.user.*;
import ca.ulaval.glo4003.ws.domain.user.credentials.PasswordAdministrator;
import ca.ulaval.glo4003.ws.domain.user.credentials.PasswordRegistry;
import ca.ulaval.glo4003.ws.infrastructure.auth.InMemorySessionRepository;
import ca.ulaval.glo4003.ws.infrastructure.user.InMemoryUserRepository;
import ca.ulaval.glo4003.ws.infrastructure.user.credentials.InMemoryPasswordRegistry;
import ca.ulaval.glo4003.ws.service.user.SessionDtoAssembler;
import ca.ulaval.glo4003.ws.service.user.UserAssembler;
import ca.ulaval.glo4003.ws.service.user.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String AUTHENTICATION_HEADER_NAME = "Bearer";
  private static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";

  @Override
  public void registerContext() {
    registerPasswordAdministrator();
    registerLocalDateProvider();
    registerRepositories();
    registerSessionServices();
    registerUserServices();
  }

  private void registerLocalDateProvider() {
    LocalDateWrapper localDateWrapper = new LocalDateWrapper();

    LocalDateProvider localDateProvider = new LocalDateProvider(localDateWrapper);
    serviceLocator.register(LocalDateProvider.class, localDateProvider);
  }

  private void registerRepositories() {
    InMemoryUserRepository userRepository = new InMemoryUserRepository();
    serviceLocator.register(UserRepository.class, userRepository);
    serviceLocator.register(UserFinder.class, userRepository);
    serviceLocator.register(SessionRepository.class, new InMemorySessionRepository());
  }

  private void registerSessionServices() {
    serviceLocator.register(SessionTokenGenerator.class, new SessionTokenGenerator());
    serviceLocator.register(TokenExtractor.class, new TokenExtractor(AUTHENTICATION_HEADER_NAME, new TokenDtoAssembler()));
    serviceLocator.register(SessionFactory.class, new SessionFactory());
    serviceLocator.register(SessionAdministrator.class, new SessionAdministrator());
    serviceLocator.register(OwnershipDomainService.class, new OwnershipDomainService());
  }

  private void registerUserServices() {
    DateParser dateParser = new DateParser(DateTimeFormatter.ofPattern(BIRTH_DATE_PATTERN));
    serviceLocator.register(BirthDateValidator.class, new BirthDateValidator(BIRTH_DATE_PATTERN));
    serviceLocator.register(UserAssembler.class, new UserAssembler(dateParser));
    serviceLocator.register(SessionDtoAssembler.class, new SessionDtoAssembler());
    serviceLocator.register(UserService.class, new UserService());

    createAccountForCatherine();
    createAccountForGuy();
  }

  private void registerPasswordAdministrator() {
    PasswordRegistry passwordRegistry = new InMemoryPasswordRegistry();
    serviceLocator.register(
        PasswordAdministrator.class, new PasswordAdministrator(passwordRegistry));
  }

  private static void createAccountForCatherine() {
    String email = "catherineleuf@evul.ulaval.ca";
    String password = "RoulezVert2021!";
    var managerUser = new User("Catherine", new BirthDate(LocalDate.of(1997, 7, 31)), "F", email);
    managerUser.addRole(Role.PRODUCTION_MANAGER);
    serviceLocator.resolve(UserRepository.class).registerUser(managerUser);
    serviceLocator.resolve(PasswordAdministrator.class).register(email, password);
  }

  private static void createAccountForGuy() {
    String email = "guy.m@evul.ulaval.ca";
    String password = "veryStrongPassword123";
    var adminUser = new User("Guy", new BirthDate(LocalDate.of(1997, 7, 31)), "M", email);
    adminUser.addRole(Role.ADMINISTRATOR);
    serviceLocator.resolve(UserRepository.class).registerUser(adminUser);
    serviceLocator.resolve(PasswordAdministrator.class).register(email, password);
  }
}
