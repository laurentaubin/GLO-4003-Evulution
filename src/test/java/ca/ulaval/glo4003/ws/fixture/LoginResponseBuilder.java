package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.api.user.response.LoginResponse;

public class LoginResponseFixture {
  private String token = "token132";

  public LoginResponseFixture withToken(String token) {
    this.token = token;
    return this;
  }

  public LoginResponse build() {
    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(token);
    return loginResponse;
  }
}
