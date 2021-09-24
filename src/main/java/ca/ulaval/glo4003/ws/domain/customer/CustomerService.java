package ca.ulaval.glo4003.ws.domain.customer;

import ca.ulaval.glo4003.ws.api.customer.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenAdministrator;
import ca.ulaval.glo4003.ws.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.exception.LoginFailedException;

public class CustomerService {
  private final CustomerRepository customerRepository;
  private final LoginTokenAdministrator loginTokenAdministrator;

  public CustomerService(
      CustomerRepository customerRepository, LoginTokenAdministrator loginTokenAdministrator) {

    this.customerRepository = customerRepository;
    this.loginTokenAdministrator = loginTokenAdministrator;
  }

  public void registerCustomer(Customer customer) {
    if (customerRepository.findCustomer(customer.getEmail()).isPresent()) {
      throw new EmailAlreadyInUseException();
    }

    customerRepository.registerCustomer(customer);
  }

  public LoginToken login(String email, String password) {
    try {
      return loginTokenAdministrator.login(email, password);
    } catch (InvalidCredentialsException ignored) {
      throw new LoginFailedException();
    }
  }
}
