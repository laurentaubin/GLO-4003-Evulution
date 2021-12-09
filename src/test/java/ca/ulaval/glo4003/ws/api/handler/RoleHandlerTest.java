package ca.ulaval.glo4003.ws.api.handler;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.fixture.UserBuilder;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleHandlerTest {
  private static final String AN_EMAIL = "anEmail@mail.com";
  private static final String A_AUTH_HEADER_NAME = "Bearer";
  private static final String A_AUTH_TOKEN_VALUE = "some_token_value";
  private static final SessionToken A_AUTH_TOKEN = new SessionToken(A_AUTH_TOKEN_VALUE);
  private static final String A_AUTH_HEADER =
      A_AUTH_HEADER_NAME + " " + A_AUTH_TOKEN.getTokenValue();
  private static final User A_USER = new UserBuilder().build();
  private static final List<Role> SOME_ROLES = List.of(Role.BASE);

  @Mock private UserRepository userRepository;
  @Mock private SessionRepository sessionRepository;
  @Mock private SessionTokenGenerator sessionTokenGenerator;
  @Mock private ContainerRequestContext aContainerRequest;
  @Mock private Session aSession;

  private final TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME);
  private RoleHandler roleHandler;

  @BeforeEach
  public void setUpRoleValidator() {
    roleHandler =
        new RoleHandler(userRepository, sessionRepository, sessionTokenGenerator, tokenExtractor);
    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_AUTH_HEADER);
  }

  @Test
  public void givenAllowedUser_whenRetrieveSession_thenReturnActiveSession() {
    // given
    givenRepositories();

    // when
    Session actualSession = roleHandler.retrieveSession(aContainerRequest, SOME_ROLES);

    // then
    assertThat(actualSession).isEqualTo(aSession);
  }

  @Test
  public void givenANotAllowedUser_whenRetrieveSession_thenAccessShouldNotBeGranted() {
    // given
    givenRepositories();
    List<Role> rolesThatTheUserDoesNotHave = List.of(Role.ADMIN);

    // when
    Executable validateRoles =
        () -> roleHandler.retrieveSession(aContainerRequest, rolesThatTheUserDoesNotHave);

    // then
    assertThrows(UnauthorizedUserException.class, validateRoles);
  }

  @Test
  public void givenAnInvalidSession_whenRetrieveSession_thenAccessShouldNotBeGranted() {
    // given
    givenInvalidSessionRepositories();

    // when
    Executable validateRoles = () -> roleHandler.retrieveSession(aContainerRequest, SOME_ROLES);

    // then
    assertThrows(SessionDoesNotExistException.class, validateRoles);
  }

  @Test
  public void givenAnInvalidUser_whenRetrieveSession_thenAccessShouldNotBeGranted() {
    // given
    givenInvalidUserRepositories();

    // when
    Executable validateRoles = () -> roleHandler.retrieveSession(aContainerRequest, SOME_ROLES);

    // then
    assertThrows(UserNotFoundException.class, validateRoles);
  }

  private void givenRepositories() {
    given(sessionTokenGenerator.generate(A_AUTH_TOKEN_VALUE)).willReturn(A_AUTH_TOKEN);
    given(sessionRepository.doesSessionExist(A_AUTH_TOKEN)).willReturn(true);
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(userRepository.findUser(AN_EMAIL)).willReturn(A_USER);
    given(sessionRepository.find(A_AUTH_TOKEN)).willReturn(aSession);
  }

  private void givenInvalidSessionRepositories() {
    given(sessionTokenGenerator.generate(A_AUTH_TOKEN_VALUE)).willReturn(A_AUTH_TOKEN);
  }

  private void givenInvalidUserRepositories() {
    given(sessionRepository.find(A_AUTH_TOKEN)).willReturn(aSession);
    given(sessionTokenGenerator.generate(A_AUTH_TOKEN_VALUE)).willReturn(A_AUTH_TOKEN);
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionRepository.doesSessionExist(A_AUTH_TOKEN)).willReturn(true);
    given(userRepository.findUser(AN_EMAIL)).willThrow(UserNotFoundException.class);
  }
}
