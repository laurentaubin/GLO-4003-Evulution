package ca.ulaval.glo4003.ws.domain.auth;

public class SessionFactory {
  private final SessionTokenGenerator tokenGenerator;

  public SessionFactory(SessionTokenGenerator tokenGenerator) {
    this.tokenGenerator = tokenGenerator;
  }

  public Session create(String email) {
    return new Session(tokenGenerator.generate(), email);
  }
}
