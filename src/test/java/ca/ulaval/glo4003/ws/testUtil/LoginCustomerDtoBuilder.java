package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.api.customer.dto.LoginCustomerDto;

public class LoginCustomerDtoBuilder {
  private String email = "email@email.com";
  private String password = "2312";

  public LoginCustomerDtoBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public LoginCustomerDtoBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public LoginCustomerDto build() {
    LoginCustomerDto loginCustomerDto = new LoginCustomerDto();
    loginCustomerDto.setEmail(email);
    loginCustomerDto.setPassword(password);

    return loginCustomerDto;
  }
}
