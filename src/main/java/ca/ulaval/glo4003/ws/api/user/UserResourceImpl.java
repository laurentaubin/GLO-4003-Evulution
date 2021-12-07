package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.service.user.dto.LoginUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import jakarta.ws.rs.core.Response;

import java.net.URI;

public class UserResourceImpl implements UserResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final URI EMPTY_URI = URI.create("");

  private final UserService userService;
  private final RequestValidator registerUserDtoValidator;

  public UserResourceImpl() {
    this(serviceLocator.resolve(UserService.class), new RequestValidator());
  }

  public UserResourceImpl(UserService userService, RequestValidator registerUserDtoValidator) {
    this.userService = userService;
    this.registerUserDtoValidator = registerUserDtoValidator;
  }

  @Override
  public Response registerUser(RegisterUserDto registerUserDto) {
    registerUserDtoValidator.validate(registerUserDto);
    userService.registerUser(registerUserDto);

    return Response.created(EMPTY_URI).build();
  }

  @Override
  public Response login(LoginUserDto loginUserDto) {
    LoginResponseDto loginResponse =
        userService.login(loginUserDto.getEmail(), loginUserDto.getPassword());
    return Response.ok(loginResponse).build();
  }
}
