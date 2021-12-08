package ca.ulaval.glo4003.ws.api.user.request;

import jakarta.validation.constraints.NotBlank;

public class LoginUserRequest {

  @NotBlank private String email;

  @NotBlank private String password;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
