package ca.ulaval.glo4003.ws.domain.customer;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.customer.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenAdministrator;
import ca.ulaval.glo4003.ws.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.testUtil.CustomerBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
  private static final String AN_EMAIL = "an@email.com";
  private static final String A_PASSWORD = "pass123";
  private static final String A_TOKEN = "token";

  @Mock private CustomerRepository customerRepository;

  @Mock private LoginTokenAdministrator loginTokenAdministrator;

  private CustomerService customerService;

  @BeforeEach
  public void setUp() {
    customerService = new CustomerService(customerRepository, loginTokenAdministrator);
  }

  @Test
  public void
      givenCustomerAlreadyExists_whenRegisterCustomer_thenThrowEmailAlreadyInUseException() {
    // given
    Customer aCustomer = new CustomerBuilder().build();
    given(customerRepository.findCustomer(aCustomer.getEmail())).willReturn(Optional.of(aCustomer));

    // when
    Executable registeringCustomer = () -> customerService.registerCustomer(aCustomer);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringCustomer);
  }

  @Test
  public void whenRegisterCustomer_thenCustomerIsRegistered() {
    // given
    Customer aCustomer = new CustomerBuilder().build();

    // when
    customerService.registerCustomer(aCustomer);

    // then
    verify(customerRepository).registerCustomer(aCustomer);
  }

  @Test
  public void givenInvalidCredentialsException_whenLogin_thenThrowLoginFailedException() {
    // given
    doThrow(new InvalidCredentialsException())
        .when(loginTokenAdministrator)
        .login(AN_EMAIL, A_PASSWORD);

    // when
    Executable loggingIn = () -> customerService.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThrows(LoginFailedException.class, loggingIn);
  }

  @Test
  public void givenSuccessfulLogin_whenLogin_thenReturnGeneratedToken() {
    // given
    LoginToken aLoginToken = new LoginToken(A_TOKEN);
    given(loginTokenAdministrator.login(AN_EMAIL, A_PASSWORD)).willReturn(aLoginToken);

    // when
    LoginToken generatedToken = customerService.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(generatedToken.getTokenValue()).matches(A_TOKEN);
  }
}
