package ca.ulaval.glo4003.ws.domain.auth;

public class Session {
  private final String tokenValue;
  private final String email;

  public Session(String token, String email) {
    this.tokenValue = token;
    this.email = email;
  }

  public String getTokenValue() {
    return tokenValue;
  }

  public String getEmail() {
    return email;
  }
}
