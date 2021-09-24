package ca.ulaval.glo4003.ws.api.customer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import ca.ulaval.glo4003.ws.api.util.DateParser;
import ca.ulaval.glo4003.ws.domain.customer.BirthDate;
import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.testUtil.RegisterCustomerDtoBuilder;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerAssemblerTest {
  private static final LocalDate A_DATE = LocalDate.of(1789, 4, 30);

  @Mock private DateParser dateParser;

  private CustomerAssembler customerAssembler;

  @BeforeEach
  public void setUp() {
    customerAssembler = new CustomerAssembler(dateParser);
  }

  @Test
  public void givenCustomerDto_whenAssemble_thenReturnCorrespondingCustomer() {
    // given
    RegisterCustomerDto aCustomerDto = new RegisterCustomerDtoBuilder().build();
    BirthDate expectedBirthDate = new BirthDate(A_DATE);
    given(dateParser.parse(aCustomerDto.getBirthDate())).willReturn(A_DATE);

    // when
    Customer actualCustomer = customerAssembler.assemble(aCustomerDto);

    // then
    assertThat(actualCustomer.getName()).matches(aCustomerDto.getName());
    assertThat(actualCustomer.getEmail()).matches(aCustomerDto.getEmail());
    assertThat(actualCustomer.getPassword()).matches(aCustomerDto.getPassword());
    assertThat(actualCustomer.getSex()).matches(aCustomerDto.getSex());
    assertThat(actualCustomer.getBirthDate()).isEqualTo(expectedBirthDate);
  }
}
