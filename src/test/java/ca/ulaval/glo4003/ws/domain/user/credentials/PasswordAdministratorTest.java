package ca.ulaval.glo4003.ws.domain.user.credentials;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PasswordAdministratorTest {
  private static final String AN_EMAIL = "anEmail@email.com";
  private static final String A_PASSWORD = "password";
  private static final String AN_INVALID_PASSWORD = "invalidPassword";

  @Mock private PasswordRegistry passwordRegistry;

  private PasswordAdministrator passwordAdministrator;

  @BeforeEach
  public void setUp() {
    passwordAdministrator = new PasswordAdministrator(passwordRegistry);
  }

  @Test
  public void givenCredentials_whenSave_thenCredentialsAreSavedInRegistry() {
    // when
    passwordAdministrator.register(AN_EMAIL, A_PASSWORD);

    // then
    verify(passwordRegistry).save(AN_EMAIL, A_PASSWORD);
  }

  @Test
  public void givenValidCredentials_whenAreCredentialsValid_thenReturnTrue() {
    // given
    given(passwordRegistry.retrievePassword(AN_EMAIL)).willReturn(Optional.of(A_PASSWORD));

    // when
    boolean valid = passwordAdministrator.areCredentialsValid(AN_EMAIL, A_PASSWORD);

    // when, then
    assertThat(valid).isTrue();
  }

  @Test
  public void givenInvalidPassword_whenAreCredentialsValid_thenReturnFalse() {
    // given
    given(passwordRegistry.retrievePassword(AN_EMAIL)).willReturn(Optional.of(A_PASSWORD));

    // when
    boolean valid = passwordAdministrator.areCredentialsValid(AN_EMAIL, AN_INVALID_PASSWORD);

    // then
    assertThat(valid).isFalse();
  }
}
