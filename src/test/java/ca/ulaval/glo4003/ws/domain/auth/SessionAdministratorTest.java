package ca.ulaval.glo4003.ws.domain.auth;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.domain.user.credentials.PasswordAdministrator;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.fixture.UserBuilder;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionAdministratorTest {
  private static final String AN_EMAIL = "an@email.com";
  private static final String A_PASSWORD = "pass123";
  private static final String AN_INVALID_PASSWORD = "invalidPassword";
  private static final String A_TOKEN_STRING = "a_token";
  private static final List<Role> USER_ROLES = List.of(Role.CUSTOMER);
  private static final List<Role> ROLES_THE_USER_DOESNT_HAVE = List.of(Role.PRODUCTION_MANAGER);

  @Mock private UserRepository userRepository;
  @Mock private SessionRepository sessionRepository;
  @Mock private SessionFactory sessionFactory;
  @Mock private Session session;
  @Mock private SessionToken sessionToken;
  @Mock private PasswordAdministrator passwordAdministrator;
  @Mock private SessionTokenGenerator sessionTokenGenerator;
  @Mock private TokenDto tokenDto;

  private User user;
  private SessionAdministrator sessionAdministrator;

  @BeforeEach
  public void setUp() {
    user = new UserBuilder().withEmail(AN_EMAIL).withRoles(USER_ROLES).build();

    sessionAdministrator =
        new SessionAdministrator(
                userRepository, sessionRepository, sessionFactory, passwordAdministrator, sessionTokenGenerator);
  }

  @Test
  public void givenUserDoesNotExist_whenLogin_thenThrowLoginFailedException() {
    // given
    given(userRepository.doesUserExist(AN_EMAIL)).willReturn(false);

    // when
    Executable checkingCredentials = () -> sessionAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThrows(LoginFailedException.class, checkingCredentials);
  }

  @Test
  public void givenUserExistsButPasswordDoesNotMatch_whenLogin_thenThrowLoginFailedException() {
    // given
    given(userRepository.doesUserExist(AN_EMAIL)).willReturn(true);
    given(passwordAdministrator.areCredentialsValid(AN_EMAIL, AN_INVALID_PASSWORD)).willReturn(false);

    // when
    Executable checkingCredentials =
        () -> sessionAdministrator.login(user.getEmail(), AN_INVALID_PASSWORD);

    // then
    assertThrows(LoginFailedException.class, checkingCredentials);
  }

  @Test
  public void givenTokenCreatedByFactory_whenLogin_thenAddTokenToPool() {
    // given
    given(userRepository.doesUserExist(AN_EMAIL)).willReturn(true);
    given(passwordAdministrator.areCredentialsValid(AN_EMAIL, A_PASSWORD)).willReturn(true);
    given(sessionFactory.create(AN_EMAIL)).willReturn(session);

    // when
    sessionAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    verify(sessionRepository).save(session);
  }

  @Test
  public void givenTokenCreatedByFactory_whenLogin_thenReturnToken() {
    // given
    given(userRepository.doesUserExist(AN_EMAIL)).willReturn(true);
    given(sessionFactory.create(AN_EMAIL)).willReturn(session);
    given(passwordAdministrator.areCredentialsValid(AN_EMAIL, A_PASSWORD)).willReturn(true);

    // when
    Session actualSession = sessionAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(actualSession).isEqualTo(session);
  }

  @Test
  public void givenDoesNotTokenExist_whenIsSessionValid_thenReturnFalse() {
    // given
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(false);

    // when
    boolean isTokenValid = sessionAdministrator.isSessionValid(sessionToken);

    // then
    assertThat(isTokenValid).isFalse();
  }

  @Test
  public void givenTokenExists_whenIsSessionValid_thenReturnTrue() {
    // given
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(true);

    // when
    boolean isTokenValid = sessionAdministrator.isSessionValid(sessionToken);

    // then
    assertThat(isTokenValid).isTrue();
  }

  @Test public void whenRegisterUser_thenRegisterAUser() {
    // when
    sessionAdministrator.registerUser(user, A_PASSWORD);

    // then
    verify(userRepository).registerUser(user);
    verify(passwordAdministrator).register(AN_EMAIL, A_PASSWORD);
  }

  @Test public void givenEmailAlreadyTaken_whenRegisterUser_thenThrowEmailAlreadyInUseException() {
    // given
    given(userRepository.doesUserExist(AN_EMAIL)).willReturn(true);

    // when
    Executable registeringUser = () -> sessionAdministrator.registerUser(user, A_PASSWORD);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringUser);
  }

  @Test public void givenUnallowedUser_whenValidatePermissions_thenThrowUnauthorizedUserException() {
    // given
    given(session.getEmail()).willReturn(AN_EMAIL);
    given(tokenDto.getToken()).willReturn(A_TOKEN_STRING);
    given(sessionTokenGenerator.generate(A_TOKEN_STRING)).willReturn(sessionToken);
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(true);
    given(sessionRepository.find(sessionToken)).willReturn(session);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    Executable authorizingUser = () -> sessionAdministrator.validatePermissions(tokenDto, ROLES_THE_USER_DOESNT_HAVE);

    // then
    assertThrows(UnauthorizedUserException.class, authorizingUser);
  }



  @Test public void whenRetrieveSession_thenReturnSession() {
    // given
    given(tokenDto.getToken()).willReturn(A_TOKEN_STRING);
    given(sessionTokenGenerator.generate(A_TOKEN_STRING)).willReturn(sessionToken);
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(true);
    given(sessionRepository.find(sessionToken)).willReturn(session);

    // when
    Session session = sessionAdministrator.retrieveSession(tokenDto);

    // then
    assertThat(session).isEqualTo(session);
  }

  @Test public void givenSessionDoesNotExists_whenRetrieveSession_thenThrowSessionDoesNotExist() {
    // given
    given(tokenDto.getToken()).willReturn(A_TOKEN_STRING);
    given(sessionTokenGenerator.generate(A_TOKEN_STRING)).willReturn(sessionToken);
    given(sessionRepository.doesSessionExist(sessionToken)).willReturn(false);

    // when
    Executable sessionExistanceCheck = () -> sessionAdministrator.retrieveSession(tokenDto);

    // then
    assertThrows(SessionDoesNotExistException.class, sessionExistanceCheck);
  }

}
