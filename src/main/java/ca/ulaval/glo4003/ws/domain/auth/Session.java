package ca.ulaval.glo4003.ws.domain.auth;

public class Session {
  private final SessionToken token;
  private final String email;

  public Session(SessionToken token, String email) {
    this.token = token;
    this.email = email;
  }

  public SessionToken getToken() {
    return token;
  }

  public String getEmail() {
    return email;
  }
}
