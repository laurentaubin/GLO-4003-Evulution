package ca.ulaval.glo4003.ws.domain.auth;

import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.domain.customer.CustomerRepository;
import ca.ulaval.glo4003.ws.domain.exception.InvalidCredentialsException;
import java.util.Optional;

public class LoginTokenAdministrator {

  private final CustomerRepository customerRepository;
  private final LoginTokenRepository loginTokenRepository;
  private final LoginTokenFactory loginTokenFactory;

  public LoginTokenAdministrator(
      CustomerRepository customerRepository,
      LoginTokenRepository loginTokenRepository,
      LoginTokenFactory loginTokenFactory) {
    this.customerRepository = customerRepository;
    this.loginTokenRepository = loginTokenRepository;
    this.loginTokenFactory = loginTokenFactory;
  }

  public LoginToken login(String email, String password) {
    Optional<Customer> customer = customerRepository.findCustomer(email);

    if (customer.isEmpty()) {
      throw new InvalidCredentialsException();
    }

    if (!customer.get().getPassword().equals(password)) {
      throw new InvalidCredentialsException();
    }

    return generateToken();
  }

  public boolean isTokenValid(LoginToken loginToken) {
    return loginTokenRepository.doesTokenExist(loginToken);
  }

  private LoginToken generateToken() {
    LoginToken loginToken = loginTokenFactory.create();
    loginTokenRepository.save(loginToken);

    return loginToken;
  }
}
