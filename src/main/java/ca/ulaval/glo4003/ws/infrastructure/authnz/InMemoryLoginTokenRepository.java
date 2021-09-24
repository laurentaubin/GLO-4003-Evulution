package ca.ulaval.glo4003.ws.infrastructure.authnz;

import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenRepository;
import java.util.HashSet;
import java.util.Set;

public class InMemoryLoginTokenRepository implements LoginTokenRepository {
  private final Set<String> tokens;

  public InMemoryLoginTokenRepository() {
    tokens = new HashSet<>();
  }

  @Override
  public void save(LoginToken loginToken) {
    tokens.add(loginToken.getTokenValue());
  }

  @Override
  public boolean doesTokenExist(LoginToken loginToken) {
    return tokens.contains(loginToken.getTokenValue());
  }
}
