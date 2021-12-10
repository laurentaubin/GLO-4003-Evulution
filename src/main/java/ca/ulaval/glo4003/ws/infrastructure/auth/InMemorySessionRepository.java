package ca.ulaval.glo4003.ws.infrastructure.auth;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;

import java.util.HashMap;
import java.util.Map;

public class InMemorySessionRepository implements SessionRepository {
  private final Map<String, Session> sessions;

  public InMemorySessionRepository() {
    sessions = new HashMap<>();
  }

  @Override
  public void save(Session session) {
    sessions.put(session.getToken().getTokenValue(), session);
  }

  @Override
  public boolean doesSessionExist(SessionToken sessionToken) {
    return sessions.containsKey(sessionToken.getTokenValue());
  }

  @Override
  public Session find(SessionToken token) {
    if (sessions.containsKey(token.getTokenValue())) {
      return sessions.get(token.getTokenValue());
    }
    throw new SessionDoesNotExistException();
  }
}
