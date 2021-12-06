package ca.ulaval.glo4003.ws.service.user;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.auth.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  private static final String AN_EMAIL = "an@email.com";
  private static final String A_PASSWORD = "pass123";
  private static final SessionToken A_TOKEN = new SessionToken("token");

  @Mock private UserRepository userRepository;
  @Mock private SessionAdministrator sessionAdministrator;
  @Mock private UserAssembler userAssembler;
  @Mock private LoginResponseAssembler loginResponseAssembler;
  @Mock private User aUser;

  private UserService userService;
  private RegisterUserDto registerUserDto;

  @BeforeEach
  public void setUp() {
    registerUserDto = createRegisterUserDto();
    userService =
        new UserService(
            userRepository, sessionAdministrator, userAssembler, loginResponseAssembler);
  }

  @Test
  public void givenUserAlreadyExists_whenRegisterUser_thenThrowEmailAlreadyInUseException() {
    // given
    given(aUser.getEmail()).willReturn(AN_EMAIL);
    given(userRepository.doesUserExist(AN_EMAIL)).willReturn(true);
    given(userAssembler.assemble(registerUserDto)).willReturn(aUser);

    // when
    Executable registeringUser = () -> userService.registerUser(registerUserDto);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringUser);
  }

  @Test
  public void whenRegisterUser_thenUserIsRegistered() {
    // given
    User aUser = new UserBuilder().build();
    given(userAssembler.assemble(registerUserDto)).willReturn(aUser);

    // when
    userService.registerUser(registerUserDto);

    // then
    verify(userRepository).registerUser(aUser);
  }

  @Test
  public void givenInvalidCredentialsException_whenLogin_thenThrowLoginFailedException() {
    // given
    doThrow(new InvalidCredentialsException())
        .when(sessionAdministrator)
        .login(AN_EMAIL, A_PASSWORD);

    // when
    Executable loggingIn = () -> userService.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThrows(LoginFailedException.class, loggingIn);
  }

  @Test
  public void givenSuccessfulLogin_whenLogin_thenReturnGeneratedToken() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);
    given(sessionAdministrator.login(AN_EMAIL, A_PASSWORD)).willReturn(aSession);
    given(loginResponseAssembler.assemble(aSession)).willReturn(createLoginResponseDto());

    // when
    LoginResponseDto loginResponseDto = userService.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(loginResponseDto.getToken()).isEqualTo(A_TOKEN.getTokenValue());
  }

  private RegisterUserDto createRegisterUserDto() {
    var registerUserDto = new RegisterUserDto();
    registerUserDto.setEmail(AN_EMAIL);
    registerUserDto.setPassword(A_PASSWORD);
    return registerUserDto;
  }

  private LoginResponseDto createLoginResponseDto() {
    var loginResponseDto = new LoginResponseDto();
    loginResponseDto.setToken(A_TOKEN.getTokenValue());
    return loginResponseDto;
  }
}
