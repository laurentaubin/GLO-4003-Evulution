package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import java.util.List;

public interface UserRepository {
  void registerUser(User user);

  User findUser(String email);

  void update(User user);

  boolean doesUserExist(String email);

  User findUserByTransactionId(TransactionId transactionId);

  List<User> findAll();
}
