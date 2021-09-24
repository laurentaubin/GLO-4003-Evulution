package ca.ulaval.glo4003.ws.domain.auth;

public interface LoginTokenRepository {
  void save(LoginToken loginToken);

  boolean doesTokenExist(LoginToken loginToken);
}
