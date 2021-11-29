package ca.ulaval.glo4003.ws.domain.auth;

import ca.ulaval.glo4003.ws.context.ServiceLocator;

public class SessionFactory {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final SessionTokenGenerator tokenGenerator;

  public SessionFactory() {
    this(serviceLocator.resolve(SessionTokenGenerator.class));
  }

  public SessionFactory(SessionTokenGenerator tokenGenerator) {
    this.tokenGenerator = tokenGenerator;
  }

  public Session create(String email) {
    return new Session(tokenGenerator.generate(), email);
  }
}
