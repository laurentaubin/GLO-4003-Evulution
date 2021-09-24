package ca.ulaval.glo4003.ws.domain.auth;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.domain.customer.CustomerRepository;
import ca.ulaval.glo4003.ws.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.testUtil.CustomerBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginTokenAdministratorTest {
  private static final String AN_EMAIL = "an@email.com";
  private static final String A_PASSWORD = "pass123";

  @Mock private CustomerRepository customerRepository;

  @Mock private LoginTokenRepository loginTokenRepository;

  @Mock private LoginTokenFactory loginTokenFactory;

  @Mock private LoginToken aLoginToken;

  private Customer aCustomer;

  private LoginTokenAdministrator loginTokenAdministrator;

  @BeforeEach
  public void setUp() {
    aCustomer = new CustomerBuilder().withEmail(AN_EMAIL).withPassword(A_PASSWORD).build();

    loginTokenAdministrator =
        new LoginTokenAdministrator(customerRepository, loginTokenRepository, loginTokenFactory);
  }

  @Test
  public void givenUserDoesNotExist_whenLogin_thenThrowLoginFailedException() {
    // given
    given(customerRepository.findCustomer(AN_EMAIL)).willReturn(Optional.empty());

    // when
    Executable checkingCredentials = () -> loginTokenAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThrows(InvalidCredentialsException.class, checkingCredentials);
  }

  @Test
  public void givenUserExistsButPasswordDoesNotMatch_whenLogin_thenThrowLoginFailedException() {
    // given
    given(customerRepository.findCustomer(AN_EMAIL)).willReturn(Optional.of(aCustomer));

    // when
    Executable checkingCredentials =
        () -> loginTokenAdministrator.login(aCustomer.getEmail(), "wrong password");

    // then
    assertThrows(InvalidCredentialsException.class, checkingCredentials);
  }

  @Test
  public void givenTokenCreatedByFactory_whenLogin_thenAddTokenToPool() {
    // given
    given(customerRepository.findCustomer(AN_EMAIL)).willReturn(Optional.of(aCustomer));
    given(loginTokenFactory.create()).willReturn(aLoginToken);

    // when
    loginTokenAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    verify(loginTokenRepository).save(aLoginToken);
  }

  @Test
  public void givenTokenCreatedByFactory_whenLogin_thenReturnToken() {
    // given
    given(customerRepository.findCustomer(AN_EMAIL)).willReturn(Optional.of(aCustomer));
    given(loginTokenFactory.create()).willReturn(aLoginToken);

    // when
    LoginToken actualToken = loginTokenAdministrator.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(actualToken).isEqualTo(aLoginToken);
  }

  @Test
  public void givenDoesNotTokenExist_whenIsTokenValid_thenReturnFalse() {
    // given
    given(loginTokenRepository.doesTokenExist(aLoginToken)).willReturn(false);

    // when
    boolean isTokenValid = loginTokenAdministrator.isTokenValid(aLoginToken);

    // then
    assertThat(isTokenValid).isFalse();
  }

  @Test
  public void givenTokenExists_whenIsTokenValid_thenReturnTrue() {
    // given
    given(loginTokenRepository.doesTokenExist(aLoginToken)).willReturn(true);

    // when
    boolean isTokenValid = loginTokenAdministrator.isTokenValid(aLoginToken);

    // then
    assertThat(isTokenValid).isTrue();
  }
}
