package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.user.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.api.user.dto.LoginUserDto;
import ca.ulaval.glo4003.ws.api.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.api.user.validator.RegisterUserDtoValidator;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserService;
import jakarta.ws.rs.core.Response;
import java.net.URI;

public class UserResourceImpl implements UserResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final URI EMPTY_URI = URI.create("");

  private final UserAssembler userAssembler;
  private final LoginResponseAssembler loginResponseAssembler;
  private final UserService userService;
  private final RegisterUserDtoValidator registerUserDtoValidator;

  public UserResourceImpl() {
    this(
        serviceLocator.resolve(UserAssembler.class),
        new LoginResponseAssembler(),
        serviceLocator.resolve(UserService.class),
        new RegisterUserDtoValidator());
  }

  public UserResourceImpl(
      UserAssembler userAssembler,
      LoginResponseAssembler loginResponseAssembler,
      UserService userService,
      RegisterUserDtoValidator registerUserDtoValidator) {

    this.userAssembler = userAssembler;
    this.loginResponseAssembler = loginResponseAssembler;
    this.userService = userService;
    this.registerUserDtoValidator = registerUserDtoValidator;
  }

  @Override
  public Response registerUser(RegisterUserDto registerUserDto) {
    registerUserDtoValidator.validateDto(registerUserDto);

    User user = userAssembler.assemble(registerUserDto);
    userService.registerUser(user);

    return Response.created(EMPTY_URI).build();
  }

  @Override
  public Response login(LoginUserDto loginUserDto) {
    Session session = userService.login(loginUserDto.getEmail(), loginUserDto.getPassword());
    LoginResponseDto loginResponseDto = loginResponseAssembler.assemble(session);

    return Response.ok(loginResponseDto).build();
  }
}
