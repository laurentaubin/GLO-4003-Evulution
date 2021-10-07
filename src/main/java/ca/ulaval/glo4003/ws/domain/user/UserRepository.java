package ca.ulaval.glo4003.ws.domain.user;

public interface UserRepository {
  void registerUser(User user);

  User findUser(String email);

  void update(User user);

  boolean doesUserExist(String email);
}
