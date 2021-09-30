package ca.ulaval.glo4003.ws.domain.auth;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionAdministratorTest {
  private static final String AN_EMAIL = "an@email.com";
  private static final String A_PASSWORD = "pass123";

  @Mock private UserRepository userRepository;

  @Mock private SessionRepository sessionRepository;

  @Mock private SessionFactory sessionFactory;

  @Mock private Session aSession;

  @Mock private SessionToken sessionToken;

  private User aUser;

  private SessionAdministrator sessionAdministrator;

  @BeforeEach
  public void setUp() {
    aUser = new UserBuilder().withEmail(AN_EMAIL).withPassword(A_PASSWORD).build();

    sessionAdministrator =
        new SessionAdministrator(userRepository, sessionRepository, sessionFactory);
  }

  @Test
  public void givenUserDoesNotExist_whenLogin_thenThrowLoginFailedException() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.empty());

    // when
    Executable checkingCredentials = () -> sessionAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThrows(InvalidCredentialsException.class, checkingCredentials);
  }

  @Test
  public void givenUserExistsButPasswordDoesNotMatch_whenLogin_thenThrowLoginFailedException() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));

    // when
    Executable checkingCredentials =
        () -> sessionAdministrator.login(aUser.getEmail(), "wrong password");

    // then
    assertThrows(InvalidCredentialsException.class, checkingCredentials);
  }

  @Test
  public void givenTokenCreatedByFactory_whenLogin_thenAddTokenToPool() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));
    given(sessionFactory.create(AN_EMAIL)).willReturn(aSession);

    // when
    sessionAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    verify(sessionRepository).save(aSession);
  }

  @Test
  public void givenTokenCreatedByFactory_whenLogin_thenReturnToken() {
    // given
    given(userRepository.findUser(AN_EMAIL)).willReturn(Optional.of(aUser));
    given(sessionFactory.create(AN_EMAIL)).willReturn(aSession);

    // when
    Session actualSession = sessionAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(actualSession).isEqualTo(aSession);
  }

  @Test
  public void givenDoesNotTokenExist_whenIsTokenValid_thenReturnFalse() {
    // given
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(false);

    // when
    boolean isTokenValid = sessionAdministrator.isSessionValid(sessionToken);

    // then
    assertThat(isTokenValid).isFalse();
  }

  @Test
  public void givenTokenExists_whenIsTokenValid_thenReturnTrue() {
    // given
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(true);

    // when
    boolean isTokenValid = sessionAdministrator.isSessionValid(sessionToken);

    // then
    assertThat(isTokenValid).isTrue();
  }
}
