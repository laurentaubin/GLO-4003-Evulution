package ca.ulaval.glo4003.ws.infrastructure.customer;

import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.domain.customer.CustomerRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository implements CustomerRepository {
  private final Map<String, Customer> customers;

  public InMemoryCustomerRepository() {
    customers = new HashMap<>();
  }

  @Override
  public void registerCustomer(Customer customer) {
    customers.put(customer.getEmail(), customer);
  }

  @Override
  public Optional<Customer> findCustomer(String email) {
    return Optional.ofNullable(customers.get(email));
  }
}
