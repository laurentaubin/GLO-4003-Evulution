package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.api.user.validator.BirthDateValidator;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateWrapper;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.auth.InMemorySessionRepository;
import ca.ulaval.glo4003.ws.infrastructure.user.InMemoryUserRepository;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.user.LoginResponseAssembler;
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
    serviceLocator.register(TokenExtractor.class, new TokenExtractor(AUTHENTICATION_HEADER_NAME));
    serviceLocator.register(SessionFactory.class, new SessionFactory());
    serviceLocator.register(SessionAdministrator.class, new SessionAdministrator());
    serviceLocator.register(RoleHandler.class, new RoleHandler());
    serviceLocator.register(OwnershipHandler.class, new OwnershipHandler());
    serviceLocator.register(AuthenticationService.class, new AuthenticationService());
  }

  private void registerUserServices() {
    DateParser dateParser = new DateParser(DateTimeFormatter.ofPattern(BIRTH_DATE_PATTERN));
    serviceLocator.register(BirthDateValidator.class, new BirthDateValidator(BIRTH_DATE_PATTERN));
    serviceLocator.register(UserAssembler.class, new UserAssembler(dateParser));
    serviceLocator.register(LoginResponseAssembler.class, new LoginResponseAssembler());
    serviceLocator.register(UserService.class, new UserService());

    createAccountForCatherine();
  }

  private static void createAccountForCatherine() {
    var adminUser =
        new User(
            "Catherine",
            new BirthDate(LocalDate.of(1997, 7, 31)),
            "F",
            "catherineleuf@evul.ulaval.ca",
            "RoulezVert2021!");
    adminUser.addRole(Role.ADMIN);
    serviceLocator.resolve(UserRepository.class).registerUser(adminUser);
  }
}
