package ca.ulaval.glo4003.ws.domain.auth;

public class SessionToken {
  private final String tokenValue;

  public SessionToken(String tokenValue) {
    this.tokenValue = tokenValue;
  }

  public String getTokenValue() {
    return tokenValue;
  }
}
