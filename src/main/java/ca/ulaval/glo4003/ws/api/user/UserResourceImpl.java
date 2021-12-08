package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.user.request.LoginUserRequest;
import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;
import jakarta.ws.rs.core.Response;
import java.net.URI;

public class UserResourceImpl implements UserResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final URI EMPTY_URI = URI.create("");

  private final UserService userService;
  private final RequestValidator registerUserDtoValidator;
  private final LoginUserDtoAssembler loginUserDtoAssembler;
  private final RegisterUserDtoAssembler registerUserDtoAssembler;

  public UserResourceImpl() {
    this(
        serviceLocator.resolve(UserService.class),
        new RequestValidator(),
        new LoginUserDtoAssembler(),
        new RegisterUserDtoAssembler());
  }

  public UserResourceImpl(
      UserService userService,
      RequestValidator registerUserDtoValidator,
      LoginUserDtoAssembler loginUserDtoAssembler,
      RegisterUserDtoAssembler registerUserDtoAssembler) {
    this.userService = userService;
    this.registerUserDtoValidator = registerUserDtoValidator;
    this.loginUserDtoAssembler = loginUserDtoAssembler;
    this.registerUserDtoAssembler = registerUserDtoAssembler;
  }

  @Override
  public Response registerUser(RegisterUserRequest registerUserRequest) {
    registerUserDtoValidator.validate(registerUserRequest);
    RegisterUserDto registerUserDto = registerUserDtoAssembler.assemble(registerUserRequest);
    userService.registerUser(registerUserDto);

    return Response.created(EMPTY_URI).build();
  }

  @Override
  public Response login(LoginUserRequest loginUserRequest) {
    SessionDto sessionDto =
        userService.login(loginUserRequest.getEmail(), loginUserRequest.getPassword());
    return Response.ok(loginUserDtoAssembler.assemble(sessionDto)).build();
  }
}
