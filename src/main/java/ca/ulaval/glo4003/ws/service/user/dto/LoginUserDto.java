package ca.ulaval.glo4003.ws.service.user.dto;

public class LoginUserDto {

  private final String email;
  private final String password;

  public LoginUserDto(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
