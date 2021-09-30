package ca.ulaval.glo4003.ws.api.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.util.TokenExtractor;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.exception.UnallowedUserException;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleValidatorTest {
  private static final String AN_EMAIL = "anEmail@mail.com";
  private static final String A_AUTH_HEADER_NAME = "Bearer";
  private static final String A_AUTH_TOKEN_VALUE = "some_token_value";
  private static final SessionToken A_AUTH_TOKEN = new SessionToken(A_AUTH_TOKEN_VALUE);
  private static final String A_AUTH_HEADER =
      A_AUTH_HEADER_NAME + " " + A_AUTH_TOKEN.getTokenValue();
  private static final Optional<User> A_USER = Optional.of(new UserBuilder().build());
  private static final Optional<User> AN_INVALID_USER = Optional.empty();
  private static final List<Role> SOME_ROLES = List.of(Role.BASE);

  private final TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME);

  @Mock private UserRepository userRepository;

  @Mock private SessionRepository sessionRepository;

  @Mock private SessionTokenGenerator sessionTokenGenerator;

  @Mock private ContainerRequestContext aContainerRequest;

  @Mock private Session aSession;

  private RoleValidator roleValidator;

  @BeforeEach
  public void setUpRoleValidator() {
    roleValidator =
        new RoleValidator(userRepository, sessionRepository, sessionTokenGenerator, tokenExtractor);
    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_AUTH_HEADER);
  }

  @Test
  public void givenAllowedUser_whenIsAllowed_thenAccessIsGranted() {
    // given
    givenRepositories();

    // then
    roleValidator.validate(aContainerRequest, SOME_ROLES);
  }

  @Test
  public void givenANotAllowedUser_whenIsAllowed_thenAccessShouldNotBeGranted() {
    // given
    givenRepositories();
    List<Role> rolesThatTheUserDoesNotHave = List.of(Role.ADMIN);

    // when
    Executable validateRoles =
        () -> roleValidator.validate(aContainerRequest, rolesThatTheUserDoesNotHave);

    // then
    assertThrows(UnallowedUserException.class, validateRoles);
  }

  @Test
  public void givenAnInvalidSession_whenIsAllowed_thenAccessShouldNotBeGranted() {
    // given
    givenInvalidSessionRepositories();

    // when
    Executable validateRoles = () -> roleValidator.validate(aContainerRequest, SOME_ROLES);

    // then
    assertThrows(SessionDoesNotExistException.class, validateRoles);
  }

  @Test
  public void givenAnInvalidUser_whenIsAllowed_thenAccessShouldNotBeGranted() {
    // given
    givenInvalidUserRepositories();

    // when
    Executable validateRoles = () -> roleValidator.validate(aContainerRequest, SOME_ROLES);

    // then
    assertThrows(UserNotFoundException.class, validateRoles);
  }

  private void givenRepositories() {
    given(sessionRepository.find(A_AUTH_TOKEN)).willReturn(Optional.of(aSession));
    given(sessionTokenGenerator.generate(A_AUTH_TOKEN_VALUE)).willReturn(A_AUTH_TOKEN);
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(userRepository.findUser(AN_EMAIL)).willReturn(A_USER);
  }

  private void givenInvalidSessionRepositories() {
    given(sessionTokenGenerator.generate(A_AUTH_TOKEN_VALUE)).willReturn(A_AUTH_TOKEN);
    given(sessionRepository.find(A_AUTH_TOKEN)).willReturn(Optional.empty());
  }

  private void givenInvalidUserRepositories() {
    given(sessionRepository.find(A_AUTH_TOKEN)).willReturn(Optional.of(aSession));
    given(sessionTokenGenerator.generate(A_AUTH_TOKEN_VALUE)).willReturn(A_AUTH_TOKEN);
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(userRepository.findUser(AN_EMAIL)).willReturn(AN_INVALID_USER);
  }
}
