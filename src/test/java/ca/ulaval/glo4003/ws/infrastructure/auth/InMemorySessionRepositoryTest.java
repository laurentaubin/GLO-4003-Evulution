package ca.ulaval.glo4003.ws.infrastructure.auth;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemorySessionRepositoryTest {
  private static final SessionToken A_TOKEN = new SessionToken("dsoadjas");
  private static final String AN_EMAIL = "blabla@blabla.com";

  private InMemorySessionRepository loginTokenRepository;

  @BeforeEach
  public void setUp() {
    loginTokenRepository = new InMemorySessionRepository();
  }

  @Test
  public void givenTokenNotSaved_whenDoesSessionExist_thenReturnFalse() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);

    // when
    boolean doesTokenExist = loginTokenRepository.doesSessionExist(A_TOKEN);

    // then
    assertThat(doesTokenExist).isFalse();
  }

  @Test
  public void givenTokenSaved_whenDoesSessionExist_thenReturnTrue() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);
    loginTokenRepository.save(aSession);

    // when
    boolean doesTokenExist = loginTokenRepository.doesSessionExist(A_TOKEN);

    // then
    assertThat(doesTokenExist).isTrue();
  }

  @Test
  public void givenAToken_whenFind_thenRetrieveAssociatedSession() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);
    loginTokenRepository.save(aSession);

    // when
    Session retrievedSession = loginTokenRepository.find(A_TOKEN).get();

    // then
    assertThat(aSession).isEqualTo(retrievedSession);
  }
}
