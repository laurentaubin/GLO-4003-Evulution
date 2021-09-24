package ca.ulaval.glo4003.ws.api.customer;

import ca.ulaval.glo4003.ws.api.customer.dto.LoginCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.validator.RegisterCustomerDtoValidator;
import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.domain.customer.CustomerService;
import jakarta.ws.rs.core.Response;
import java.net.URI;

public class CustomerResourceImpl implements CustomerResource {
  private static final URI EMPTY_URI = URI.create("");

  private final CustomerAssembler customerAssembler;
  private final LoginResponseAssembler loginResponseAssembler;
  private final CustomerService customerService;
  private final RegisterCustomerDtoValidator registerCustomerDtoValidator;

  public CustomerResourceImpl(
      CustomerAssembler customerAssembler,
      LoginResponseAssembler loginResponseAssembler,
      CustomerService customerService,
      RegisterCustomerDtoValidator registerCustomerDtoValidator) {

    this.customerAssembler = customerAssembler;
    this.loginResponseAssembler = loginResponseAssembler;
    this.customerService = customerService;
    this.registerCustomerDtoValidator = registerCustomerDtoValidator;
  }

  @Override
  public Response registerCustomer(RegisterCustomerDto registerCustomerDto) {
    registerCustomerDtoValidator.validateDto(registerCustomerDto);

    Customer customer = customerAssembler.assemble(registerCustomerDto);
    customerService.registerCustomer(customer);

    return Response.created(EMPTY_URI).build();
  }

  @Override
  public Response login(LoginCustomerDto loginCustomerDto) {
    LoginToken loginToken =
        customerService.login(loginCustomerDto.getEmail(), loginCustomerDto.getPassword());
    LoginResponseDto loginResponseDto = loginResponseAssembler.assemble(loginToken);

    return Response.ok(loginResponseDto).build();
  }
}
