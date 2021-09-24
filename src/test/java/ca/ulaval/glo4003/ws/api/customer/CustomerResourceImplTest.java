package ca.ulaval.glo4003.ws.api.customer;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.customer.dto.LoginCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.api.customer.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.customer.validator.RegisterCustomerDtoValidator;
import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import ca.ulaval.glo4003.ws.domain.customer.Customer;
import ca.ulaval.glo4003.ws.domain.customer.CustomerService;
import ca.ulaval.glo4003.ws.domain.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.testUtil.CustomerBuilder;
import ca.ulaval.glo4003.ws.testUtil.LoginCustomerDtoBuilder;
import ca.ulaval.glo4003.ws.testUtil.LoginResponseDtoBuilder;
import ca.ulaval.glo4003.ws.testUtil.RegisterCustomerDtoBuilder;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerResourceImplTest {
  private static final String A_TOKEN = "token123";

  @Mock private CustomerAssembler customerAssembler;

  @Mock private LoginResponseAssembler loginResponseAssembler;

  @Mock private CustomerService customerService;

  @Mock private RegisterCustomerDtoValidator registerCustomerDtoValidator;

  private CustomerResourceImpl customerResource;

  @BeforeEach
  public void setUp() {
    customerResource =
        new CustomerResourceImpl(
            customerAssembler,
            loginResponseAssembler,
            customerService,
            registerCustomerDtoValidator);
  }

  @Test
  public void givenValidRegisterCustomerDto_whenRegisterCustomer_thenCustomerIsRegistered() {
    // given
    RegisterCustomerDto aCustomerDto = new RegisterCustomerDtoBuilder().build();
    Customer aCustomer = new CustomerBuilder().build();
    given(customerAssembler.assemble(aCustomerDto)).willReturn(aCustomer);

    // when
    customerResource.registerCustomer(aCustomerDto);

    // then
    verify(customerService).registerCustomer(aCustomer);
  }

  @Test
  public void
      givenInvalidRegisterCustomerDto_whenRegisterCustomer_thenThrowInvalidFormatException() {
    // given
    RegisterCustomerDto aCustomerDto = new RegisterCustomerDtoBuilder().build();
    doThrow(InvalidFormatException.class)
        .when(registerCustomerDtoValidator)
        .validateDto(aCustomerDto);

    // when
    Executable registeringCustomer = () -> customerResource.registerCustomer(aCustomerDto);

    // then
    assertThrows(InvalidFormatException.class, registeringCustomer);
  }

  @Test
  public void
      givenEmailAlreadyAssociatedToCustomer_whenRegisterCustomer_thenThrowEmailAlreadyInUseException() {
    // given
    RegisterCustomerDto aCustomerDto = new RegisterCustomerDtoBuilder().build();
    Customer aCustomer = new CustomerBuilder().build();
    given(customerAssembler.assemble(aCustomerDto)).willReturn(aCustomer);
    doThrow(EmailAlreadyInUseException.class).when(customerService).registerCustomer(aCustomer);

    // when
    Executable registeringCustomer = () -> customerResource.registerCustomer(aCustomerDto);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringCustomer);
  }

  @Test
  public void givenSuccessfulLogin_whenLogin_thenReturn200WithLoginToken() {
    // given
    LoginCustomerDto aLoginDto = new LoginCustomerDtoBuilder().build();
    LoginResponseDto aLoginResponseDto = new LoginResponseDtoBuilder().build();
    LoginToken aLoginToken = new LoginToken(A_TOKEN);
    given(customerService.login(aLoginDto.getEmail(), aLoginDto.getPassword()))
        .willReturn(aLoginToken);
    given(loginResponseAssembler.assemble(aLoginToken)).willReturn(aLoginResponseDto);

    // when
    Response response = customerResource.login(aLoginDto);
    LoginResponseDto actualLoginResponseDto = (LoginResponseDto) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(actualLoginResponseDto.getToken()).matches(aLoginResponseDto.getToken());
  }

  @Test
  public void givenWrongEmailPasswordCombination_whenLogin_thenThrowLoginFailedException() {
    // given
    LoginCustomerDto aLoginDto = new LoginCustomerDtoBuilder().build();
    given(customerService.login(aLoginDto.getEmail(), aLoginDto.getPassword()))
        .willThrow(new LoginFailedException());

    // when
    Executable loggingIn = () -> customerResource.login(aLoginDto);

    // then
    assertThrows(LoginFailedException.class, loggingIn);
  }
}
