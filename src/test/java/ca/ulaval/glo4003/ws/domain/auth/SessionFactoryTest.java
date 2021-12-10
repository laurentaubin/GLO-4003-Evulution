package ca.ulaval.glo4003.ws.domain.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SessionFactoryTest {

  private static final String AN_EMAIL = "ANEMAIL@ASD.com";
  private static final SessionToken A_TOKEN = new SessionToken("a token");

  @Mock private SessionTokenGenerator tokenGenerator;

  private SessionFactory sessionFactory;

  @BeforeEach
  public void setUp() {
    sessionFactory = new SessionFactory(tokenGenerator);
  }

  @Test
  public void whenCreate_thenCreateLoginToken() {
    // given
    given(tokenGenerator.generate()).willReturn(A_TOKEN);

    // when
    Session session = sessionFactory.create(AN_EMAIL);

    // then
    assertThat(session.getToken()).isEqualTo(A_TOKEN);
    assertThat(session.getEmail()).matches(AN_EMAIL);
  }
}
