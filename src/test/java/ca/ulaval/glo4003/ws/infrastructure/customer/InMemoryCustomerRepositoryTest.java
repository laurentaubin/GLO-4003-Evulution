package ca.ulaval.glo4003.ws.infrastructure.customer;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.testUtil.CustomerBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryCustomerRepositoryTest {
  private InMemoryCustomerRepository customerRepository;

  @BeforeEach
  void setUp() {
    customerRepository = new InMemoryCustomerRepository();
  }

  @Test
  void whenRegisterCustomer_thenCustomerStored() {
    // given
    Customer aCustomer = new CustomerBuilder().build();

    // when
    customerRepository.registerCustomer(aCustomer);
    Customer actualCustomer = customerRepository.findCustomer(aCustomer.getEmail()).get();

    // then
    assertThat(actualCustomer).isEqualTo(aCustomer);
  }

  @Test
  public void givenCustomerNotPresent_whenFindCustomer_thenReturnEmptyOptional() {
    // when
    Optional<Customer> noCustomer = customerRepository.findCustomer("a random email");

    // then
    assertThat(noCustomer.isEmpty()).isTrue();
  }
}
