package ca.ulaval.glo4003.ws.api.customer;

import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import ca.ulaval.glo4003.ws.api.util.DateParser;
import ca.ulaval.glo4003.ws.domain.customer.BirthDate;
import ca.ulaval.glo4003.ws.domain.customer.Customer;
import java.time.LocalDate;

public class CustomerAssembler {
  private final DateParser dateParser;

  public CustomerAssembler(DateParser dateParser) {
    this.dateParser = dateParser;
  }

  public Customer assemble(RegisterCustomerDto registerCustomerDto) {
    LocalDate localBirthDate = dateParser.parse(registerCustomerDto.getBirthDate());
    BirthDate birthDate = new BirthDate(localBirthDate);

    return new Customer(
        registerCustomerDto.getName(),
        birthDate,
        registerCustomerDto.getSex(),
        registerCustomerDto.getEmail(),
        registerCustomerDto.getPassword());
  }
}
