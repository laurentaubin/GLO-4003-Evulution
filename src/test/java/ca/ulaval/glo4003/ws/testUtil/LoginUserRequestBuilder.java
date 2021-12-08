package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.api.user.request.LoginUserRequest;

public class LoginUserRequestBuilder {
  private String email = "email@email.com";
  private String password = "2312";

  public LoginUserRequestBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public LoginUserRequestBuilder withPassword(String password) {
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
