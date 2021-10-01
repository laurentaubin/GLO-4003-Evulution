package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {
  private final Map<String, User> users;

  public InMemoryUserRepository() {
    users = new HashMap<>();
  }

  @Override
  public void registerUser(User user) {
    users.put(user.getEmail(), user);
  }

  @Override
  public Optional<User> findUser(String email) {
    return Optional.ofNullable(users.get(email));
  }

  @Override
  public void update(User user) {
    users.put(user.getEmail(), user);
  }
}
