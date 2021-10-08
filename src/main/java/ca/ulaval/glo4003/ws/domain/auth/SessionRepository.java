package ca.ulaval.glo4003.ws.domain.auth;

public interface SessionRepository {
  void save(Session session);

  boolean doesSessionExist(SessionToken sessionToken);

  Session find(SessionToken token);
}
