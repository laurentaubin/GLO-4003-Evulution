package ca.ulaval.glo4003.ws.domain.customer;

import java.util.Optional;

public interface CustomerRepository {
  void registerCustomer(Customer customer);

  Optional<Customer> findCustomer(String email);
}
