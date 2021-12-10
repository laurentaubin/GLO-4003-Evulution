package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.infrastructure.user.credentials.InMemoryPasswordRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;

class InMemoryPasswordRegistryTest {
  private static final String EMAIL = "anEmail@email.com";
  private static final Optional<String> PASSWORD = Optional.of("password");

  private InMemoryPasswordRegistry inMemoryPasswordRegistry;

  @BeforeEach
  public void setUp() {
    inMemoryPasswordRegistry = new InMemoryPasswordRegistry();
  }

  @Test
  public void givenAnEmailAndPassword_whenSave_thenCredentialsAreSaved() {
    // when
    inMemoryPasswordRegistry.save(EMAIL, PASSWORD.get());

    // then
    assertThat(inMemoryPasswordRegistry.retrievePassword(EMAIL)).isEqualTo(PASSWORD);
  }

  @Test
  public void givenNonExistentCredentials_whenRetrievePassword_thenReturnEmptyEmail() {
    // when
    Optional<String> retrievedEmail = inMemoryPasswordRegistry.retrievePassword(EMAIL);

    // then
    assertThat(retrievedEmail.isEmpty()).isTrue();
  }
}
