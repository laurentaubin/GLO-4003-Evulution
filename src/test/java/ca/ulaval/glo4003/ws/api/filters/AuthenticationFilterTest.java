package ca.ulaval.glo4003.ws.api.filters;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.filters.secured.AuthenticationFilter;
import ca.ulaval.glo4003.ws.api.util.TokenExtractor;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {
  private static final String AN_EMAIL = "anEmail@mail.com";
  private static final String A_AUTH_HEADER_NAME = "Bearer";
  private static final String A_VALID_AUTH_TOKEN = "some_token_value";
  private static final String A_VALID_AUTH_HEADER = A_AUTH_HEADER_NAME + " " + A_VALID_AUTH_TOKEN;
  private static final Session A_SESSION = new Session(A_VALID_AUTH_TOKEN, AN_EMAIL);

  private TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME);

  @Mock ContainerRequestContext aContainerRequest;

  @Mock private SessionFactory sessionFactory;

  @Mock private SessionAdministrator sessionAdministrator;

  private AuthenticationFilter authenticationFilter;

  @BeforeEach
  public void setUp() {
    authenticationFilter =
        new AuthenticationFilter(
            A_AUTH_HEADER_NAME, sessionFactory, sessionAdministrator, tokenExtractor);
  }

  @Test
  public void givenAuthorizationHeaderNonPresent_whenFilter_thenAbortWith401() throws IOException {
    // given
    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(null);

    // when
    authenticationFilter.filter(aContainerRequest);

    // then
    ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
    verify(aContainerRequest).abortWith(response.capture());
    assertThat(response.getValue().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  public void givenTokenWithWrongHeaderName_whenFilter_thenAbortWith401() throws IOException {
    // given
    String wrongHeader = "Beerer " + A_VALID_AUTH_TOKEN;
    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(wrongHeader);

    // when
    authenticationFilter.filter(aContainerRequest);

    // then
    ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
    verify(aContainerRequest).abortWith(response.capture());
    assertThat(response.getValue().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  public void givenInvalidSession_whenFilter_thenAbortWith401() throws IOException {
    // given
    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION))
        .willReturn(A_VALID_AUTH_HEADER);
    given(sessionFactory.create(A_VALID_AUTH_TOKEN)).willReturn(A_SESSION);
    given(sessionAdministrator.isSessionValid(A_SESSION)).willReturn(false);

    // when
    authenticationFilter.filter(aContainerRequest);

    // then
    ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
    verify(aContainerRequest).abortWith(response.capture());
    assertThat(response.getValue().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  public void givenValidHeaderAndToken_whenFilter_thenDoNotAbortRequest() throws IOException {
    // given
    given(aContainerRequest.getHeaderString(HttpHeaders.AUTHORIZATION))
        .willReturn(A_VALID_AUTH_HEADER);
    given(sessionFactory.create(A_VALID_AUTH_TOKEN)).willReturn(A_SESSION);
    given(sessionAdministrator.isSessionValid(A_SESSION)).willReturn(true);

    // when
    authenticationFilter.filter(aContainerRequest);

    // then
    verify(aContainerRequest, never()).abortWith(any());
  }
}
