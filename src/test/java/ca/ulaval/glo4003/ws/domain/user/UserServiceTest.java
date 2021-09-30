package ca.ulaval.glo4003.ws.domain.user;

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
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.util.Optional;
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

  private UserService userService;

  @BeforeEach
  public void setUp() {
    userService = new UserService(userRepository, sessionAdministrator);
  }

  @Test
  public void givenUserAlreadyExists_whenRegisterUser_thenThrowEmailAlreadyInUseException() {
    // given
    User aUser = new UserBuilder().build();
    given(userRepository.findUser(aUser.getEmail())).willReturn(Optional.of(aUser));

    // when
    Executable registeringUser = () -> userService.registerUser(aUser);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringUser);
  }

  @Test
  public void whenRegisterUser_thenUserIsRegistered() {
    // given
    User aUser = new UserBuilder().build();

    // when
    userService.registerUser(aUser);

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

    // when
    Session generatedSession = userService.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(generatedSession.getToken()).isEqualTo(A_TOKEN);
  }
}
