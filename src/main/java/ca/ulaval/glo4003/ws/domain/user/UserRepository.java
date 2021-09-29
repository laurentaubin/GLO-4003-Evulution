package ca.ulaval.glo4003.ws.domain.user;

import java.util.Optional;

public interface UserRepository {
  void registerUser(User user);

  Optional<User> findUser(String email);
}
