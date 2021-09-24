package ca.ulaval.glo4003.ws.infrastructure.authnz;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryLoginTokenRepositoryTest {
  private static final String A_TOKEN = "dsoadjas";

  private InMemoryLoginTokenRepository loginTokenRepository;

  @BeforeEach
  public void setUp() {
    loginTokenRepository = new InMemoryLoginTokenRepository();
  }

  @Test
  public void givenTokenNotSaved_whenCheckingIfTokenIsValid_thenReturnFalse() {
    // given
    LoginToken aToken = new LoginToken(A_TOKEN);

    // when
    boolean doesTokenExist = loginTokenRepository.doesTokenExist(aToken);

    // then
    assertThat(doesTokenExist).isFalse();
  }

  @Test
  public void givenTokenSaved_whenCheckingIfTokenIsValid_thenReturnTrue() {
    // given
    LoginToken aToken = new LoginToken(A_TOKEN);
    loginTokenRepository.save(aToken);

    // when
    boolean doesTokenExist = loginTokenRepository.doesTokenExist(aToken);

    // then
    assertThat(doesTokenExist).isTrue();
  }
}
