package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;

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
  public User findUser(String email) {
    if (users.containsKey(email)) {
      return users.get(email);
    }
    throw new UserNotFoundException();
  }

  @Override
  public void update(User user) {
    users.put(user.getEmail(), user);
  }

  @Override
  public boolean doesUserExist(String email) {
    return users.containsKey(email);
  }

  @Override
  public User findUserByTransactionId(TransactionId transactionId) {
    return users.values().stream()
        .filter(user -> user.doesOwnTransaction(transactionId))
        .findFirst()
        .get();
  }
}
