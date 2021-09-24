package ca.ulaval.glo4003.ws.domain.auth;

public class LoginToken {
  private final String tokenValue;

  public LoginToken(String token) {
    this.tokenValue = token;
  }

  public String getTokenValue() {
    return tokenValue;
  }
}
