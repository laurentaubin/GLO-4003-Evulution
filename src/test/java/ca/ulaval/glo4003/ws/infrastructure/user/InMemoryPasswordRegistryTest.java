package ca.ulaval.glo4003.ws.infrastructure.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.infrastructure.user.credentials.InMemoryPasswordRegistry;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryPasswordRegistryTest {
  private static final String AN_EMAIL = "anEmail@email.com";
  private static final Optional<String> A_PASSWORD = Optional.of("password");

  private InMemoryPasswordRegistry inMemoryPasswordRegistry;

  @BeforeEach
  public void setUp() {
    inMemoryPasswordRegistry = new InMemoryPasswordRegistry();
  }

  @Test
  public void givenAnEmailAndPassword_whenSave_thenCredentialsAreSaved() {
    // when
    inMemoryPasswordRegistry.save(AN_EMAIL, A_PASSWORD.get());

    // then
    assertThat(inMemoryPasswordRegistry.retrievePassword(AN_EMAIL)).isEqualTo(A_PASSWORD);
  }

  @Test
  public void givenNonExistentCredentials_whenRetrievePassword_thenReturnEmptyEmail() {
    // when
    Optional<String> retrievedEmail = inMemoryPasswordRegistry.retrievePassword(AN_EMAIL);

    // then
    assertThat(retrievedEmail.isEmpty()).isTrue();
  }
}
