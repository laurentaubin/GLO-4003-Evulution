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
import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;

public class UserService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final UserRepository userRepository;
  private final SessionAdministrator sessionAdministrator;
  private final UserAssembler userAssembler;
  private final LoginResponseAssembler loginResponseAssembler;
  private final PasswordAdministrator passwordAdministrator;

  public UserService() {
    this(
        serviceLocator.resolve(UserRepository.class),
        serviceLocator.resolve(SessionAdministrator.class),
        serviceLocator.resolve(UserAssembler.class),
        new LoginResponseAssembler(),
        serviceLocator.resolve(PasswordAdministrator.class));
  }

  public UserService(
      UserRepository userRepository,
      SessionAdministrator sessionAdministrator,
      UserAssembler userAssembler,
      LoginResponseAssembler loginResponseAssembler,
      PasswordAdministrator passwordAdministrator) {
    this.userRepository = userRepository;
    this.sessionAdministrator = sessionAdministrator;
    this.userAssembler = userAssembler;
    this.loginResponseAssembler = loginResponseAssembler;
    this.passwordAdministrator = passwordAdministrator;
  }

  public void registerUser(RegisterUserDto registerUserDto) {
    User user = userAssembler.assemble(registerUserDto);
    if (userRepository.doesUserExist(user.getEmail())) {
      throw new EmailAlreadyInUseException();
    }
    userRepository.registerUser(user);
    passwordAdministrator.register(registerUserDto.getEmail(), registerUserDto.getPassword());
  }

  public LoginResponseDto login(String email, String password) {
    try {
      Session session = sessionAdministrator.login(email, password);
      return loginResponseAssembler.assemble(session);
    } catch (InvalidCredentialsException ignored) {
      throw new LoginFailedException();
    }
  }
}
