package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public interface UserRepository {
  void registerUser(User user);

  User findUser(String email);

  void update(User user);

  boolean doesUserExist(String email);

  User findUserByTransactionId(TransactionId transactionId);
}
