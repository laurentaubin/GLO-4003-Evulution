package ca.ulaval.glo4003.ws.domain.auth;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionFactoryTest {

  private static final String AN_EMAIL = "ANEMAIL@ASD.com";

  private SessionFactory sessionFactory;

  @BeforeEach
  public void setUp() {
    sessionFactory = new SessionFactory();
  }

  @Test
  public void whenCreate_thenCreateLoginToken() {
    // when
    Session session = sessionFactory.create(AN_EMAIL);

    // then
    assertThat(session.getTokenValue()).isNotEmpty();
  }

  @Test
  public void givenTokenValue_whenCreate_thenCreateLoginToken() {
    // given
    String aTokenValue = "asdoaskd";

    // when
    Session session = sessionFactory.create(aTokenValue, AN_EMAIL);

    // then
    assertThat(session.getTokenValue()).matches(aTokenValue);
  }
}
