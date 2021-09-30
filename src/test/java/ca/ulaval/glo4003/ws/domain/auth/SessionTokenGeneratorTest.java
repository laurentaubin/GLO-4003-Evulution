package ca.ulaval.glo4003.ws.domain.auth;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionTokenGeneratorTest {

  private SessionTokenGenerator tokenGenerator;

  @BeforeEach
  public void setUp() {
    tokenGenerator = new SessionTokenGenerator();
  }

  @Test
  public void whenGenerate_thenGenerateSessionToken() {
    // when
    SessionToken sessionToken = tokenGenerator.generate();

    // then
    assertThat(sessionToken.getTokenValue()).isNotEmpty();
  }

  @Test
  public void givenTokenValue_whenGenerate_thenGenerateSessionTokenWithRightValue() {
    // given
    String tokenValue = "sdoasjdia";

    // when
    SessionToken sessionToken = tokenGenerator.generate(tokenValue);

    // then
    assertThat(sessionToken.getTokenValue()).matches(tokenValue);
  }
}
