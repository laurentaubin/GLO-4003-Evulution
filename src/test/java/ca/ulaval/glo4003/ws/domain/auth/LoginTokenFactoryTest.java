package ca.ulaval.glo4003.ws.domain.auth;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginTokenFactoryTest {

  private LoginTokenFactory loginTokenFactory;

  @BeforeEach
  public void setUp() {
    loginTokenFactory = new LoginTokenFactory();
  }

  @Test
  public void whenCreate_thenCreateLoginToken() {
    // when
    LoginToken loginToken = loginTokenFactory.create();

    // then
    assertThat(loginToken.getTokenValue()).isNotEmpty();
  }

  @Test
  public void givenTokenValue_whenCreate_thenCreateLoginToken() {
    // given
    String aTokenValue = "asdoaskd";

    // when
    LoginToken loginToken = loginTokenFactory.create(aTokenValue);

    // then
    assertThat(loginToken.getTokenValue()).matches(aTokenValue);
  }
}
