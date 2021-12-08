package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.domain.user.credentials.PasswordAdministrator;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;

public class UserService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final UserRepository userRepository;
  private final SessionAdministrator sessionAdministrator;
  private final UserAssembler userAssembler;
  private final SessionDtoAssembler sessionDtoAssembler;
  private final PasswordAdministrator passwordAdministrator;

  public UserService() {
    this(
        serviceLocator.resolve(UserRepository.class),
        serviceLocator.resolve(SessionAdministrator.class),
        serviceLocator.resolve(UserAssembler.class),
        new SessionDtoAssembler(),
        serviceLocator.resolve(PasswordAdministrator.class));
  }

  public UserService(
      UserRepository userRepository,
      SessionAdministrator sessionAdministrator,
      UserAssembler userAssembler,
      SessionDtoAssembler sessionDtoAssembler,
      PasswordAdministrator passwordAdministrator) {
    this.userRepository = userRepository;
    this.sessionAdministrator = sessionAdministrator;
    this.userAssembler = userAssembler;
    this.sessionDtoAssembler = sessionDtoAssembler;
    this.passwordAdministrator = passwordAdministrator;
  }

  public void registerUser(RegisterUserDto registerUserDto) {
    User user = userAssembler.assemble(registerUserDto);
    if (userRepository.doesUserExist(user.getEmail())) {
      throw new EmailAlreadyInUseException();
    }
    userRepository.registerUser(user);
    passwordAdministrator.register(registerUserDto.email, registerUserDto.password);
  }

  public SessionDto login(String email, String password) {
    try {
      Session session = sessionAdministrator.login(email, password);
      return sessionDtoAssembler.assemble(session);
    } catch (InvalidCredentialsException ignored) {
      throw new LoginFailedException();
    }
  }
}
