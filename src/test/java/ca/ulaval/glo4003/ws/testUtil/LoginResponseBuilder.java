package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.api.user.response.LoginResponse;

public class LoginResponseBuilder {
  private String token = "token132";

  public LoginResponseBuilder withToken(String token) {
    this.token = token;
    return this;
  }

  public LoginResponse build() {
    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(token);
    return loginResponse;
  }
}
