package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.api.user.request.LoginUserRequest;

public class LoginUserRequestFixture {
  private String email = "email@email.com";
  private String password = "2312";

  public LoginUserRequestFixture withEmail(String email) {
    this.email = email;
    return this;
  }

  public LoginUserRequestFixture withPassword(String password) {
    this.password = password;
    return this;
  }

  public LoginUserRequest build() {
    LoginUserRequest loginUserRequest = new LoginUserRequest();
    loginUserRequest.setEmail(email);
    loginUserRequest.setPassword(password);

    return loginUserRequest;
  }
}
