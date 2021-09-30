// package ca.ulaval.glo4003.ws.api.filters;
//
// import static com.google.common.truth.Truth.assertThat;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.verify;
//
// import ca.ulaval.glo4003.ws.api.filters.allowed.Allowed;
// import ca.ulaval.glo4003.ws.api.filters.allowed.RoleFilter;
// import ca.ulaval.glo4003.ws.domain.auth.Session;
// import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
// import ca.ulaval.glo4003.ws.domain.user.Role;
// import ca.ulaval.glo4003.ws.domain.user.User;
// import ca.ulaval.glo4003.ws.domain.user.UserRepository;
// import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
// import jakarta.ws.rs.container.ContainerRequestContext;
// import jakarta.ws.rs.core.HttpHeaders;
// import jakarta.ws.rs.core.Response;
// import jakarta.ws.rs.core.Response.Status;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class RoleFilterTest {
//  private static final String AN_EMAIL = "anEmail@mail.com";
//  private static final String A_AUTH_HEADER_NAME = "Bearer";
//  private static final String A_VALID_AUTH_TOKEN = "some_token_value";
//  private static final String A_VALID_AUTH_HEADER = A_AUTH_HEADER_NAME + " " + A_VALID_AUTH_TOKEN;
//  private static final Session A_SESSION = new Session(A_VALID_AUTH_TOKEN, AN_EMAIL);
//  private static final List<Role> NO_ROLE = new ArrayList<>();
//  private static final Optional<User> A_USER =
//      Optional.of(new UserBuilder().withEmail(AN_EMAIL).withRoles(NO_ROLE).build());
//
//  @Mock private UserRepository userRepository;
//
//  @Mock private SessionRepository sessionRepository;
//
//  @Mock ContainerRequestContext aContainerRequest;
//
//  private RoleFilter roleFilter;
//
//  @BeforeEach
//  public void setUp() {
//    roleFilter = new RoleFilter(userRepository, sessionRepository, A_AUTH_HEADER_NAME);
//  }
//
//  @Test
//  public void givenNotAllowed_whenFilter_thenAbortWithUnauthorized() {
//    // given
//    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION))
//        .willReturn(A_VALID_AUTH_HEADER);
//    given(sessionRepository.find(A_VALID_AUTH_TOKEN)).willReturn(A_SESSION);
//    given(userRepository.findUser(AN_EMAIL)).willReturn(A_USER);
//
//    // when
//    roleFilter.filter(aContainerRequest);
//
//    // then
//    ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
//    verify(aContainerRequest).abortWith(response.capture());
//    assertThat(response.getValue().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
//  }
//
//  private class Wrapped {
//    @Allowed(roles = {"ADMIN"})
//    public Response adminRoute() {
//      return Response.ok().build();
//    }
//  }
// }
