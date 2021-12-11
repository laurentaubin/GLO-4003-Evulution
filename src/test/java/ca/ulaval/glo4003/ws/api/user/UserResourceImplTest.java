package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.api.user.request.LoginUserRequest;
import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import ca.ulaval.glo4003.ws.api.user.response.LoginResponse;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.fixture.LoginResponseBuilder;
import ca.ulaval.glo4003.ws.fixture.LoginUserRequestBuilder;
import ca.ulaval.glo4003.ws.fixture.RegisterUserRequestBuilder;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserResourceImplTest {
  private static final String A_TOKEN = "token";

  @Mock private UserService userService;
  @Mock private RequestValidator registerUserDtoValidator;
  @Mock private LoginUserDtoAssembler loginUserDtoAssembler;
  @Mock private RegisterUserDtoAssembler registerUserDtoAssembler;
  @Mock private RegisterUserDto registerUserDto;

  private UserResourceImpl userResource;

  @BeforeEach
  public void setUp() {
    userResource =
        new UserResourceImpl(
            userService, registerUserDtoValidator, loginUserDtoAssembler, registerUserDtoAssembler);
  }

  @Test
  public void givenValidRegisterUserDto_whenRegisterUser_thenUserIsRegistered() {
    // given
    RegisterUserRequest request = new RegisterUserRequestBuilder().build();
    given(registerUserDtoAssembler.assemble(request)).willReturn(registerUserDto);

    // when
    userResource.registerUser(request);

    // then
    verify(userService).registerUser(registerUserDto);
  }

  @Test
  public void givenInvalidRegisterUserDto_whenRegisterUser_thenThrowInvalidFormatException() {
    // given
    RegisterUserRequest request = new RegisterUserRequestBuilder().build();
    doThrow(InvalidFormatException.class).when(registerUserDtoValidator).validate(request);

    // when
    Executable registeringUser = () -> userResource.registerUser(request);

    // then
    assertThrows(InvalidFormatException.class, registeringUser);
  }

  @Test
  public void
      givenEmailAlreadyAssociatedToUser_whenRegisterUser_thenThrowEmailAlreadyInUseException() {
    // given
    RegisterUserRequest request = new RegisterUserRequestBuilder().build();
    given(registerUserDtoAssembler.assemble(request)).willReturn(registerUserDto);
    doThrow(EmailAlreadyInUseException.class).when(userService).registerUser(registerUserDto);

    // when
    Executable registeringUser = () -> userResource.registerUser(request);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringUser);
  }

  @Test
  public void givenSuccessfulLogin_whenLogin_thenReturn200WithLoginToken() {
    // given
    LoginUserRequest request = new LoginUserRequestBuilder().build();
    SessionDto sessionDto = new SessionDto(A_TOKEN);
    LoginResponse loginResponse = new LoginResponseBuilder().build();
    given(userService.login(request.getEmail(), request.getPassword())).willReturn(sessionDto);
    given(loginUserDtoAssembler.assemble(sessionDto)).willReturn(loginResponse);

    // when
    Response response = userResource.login(request);
    LoginResponse actualLoginResponse = (LoginResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(actualLoginResponse.getToken()).matches(loginResponse.getToken());
  }

  @Test
  public void givenWrongEmailPasswordCombination_whenLogin_thenThrowLoginFailedException() {
    // given
    LoginUserRequest request = new LoginUserRequestBuilder().build();
    given(userService.login(request.getEmail(), request.getPassword()))
        .willThrow(new LoginFailedException());

    // when
    Executable loggingIn = () -> userResource.login(request);

    // then
    assertThrows(LoginFailedException.class, loggingIn);
  }
}
