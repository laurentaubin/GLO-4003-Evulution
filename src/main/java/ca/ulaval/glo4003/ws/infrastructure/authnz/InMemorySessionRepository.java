package ca.ulaval.glo4003.ws.infrastructure.authnz;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemorySessionRepository implements SessionRepository {
  private final Map<String, Session> sessions;

  public InMemorySessionRepository() {
    sessions = new HashMap<>();
  }

  @Override
  public void save(Session session) {
    sessions.put(session.getTokenValue(), session);
  }

  @Override
  public boolean doesSessionExist(Session session) {
    return sessions.containsKey(session.getTokenValue());
  }

  @Override
  public Optional<Session> find(String token) {
    return Optional.ofNullable(sessions.get(token));
  }
}
