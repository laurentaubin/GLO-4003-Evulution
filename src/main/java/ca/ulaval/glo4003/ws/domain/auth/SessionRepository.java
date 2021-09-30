package ca.ulaval.glo4003.ws.domain.auth;

import java.util.Optional;

public interface SessionRepository {
  void save(Session session);

  boolean doesSessionExist(SessionToken sessionToken);

  Optional<Session> find(SessionToken token);
}
