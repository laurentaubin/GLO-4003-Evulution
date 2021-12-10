package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

import java.util.Collection;

public interface UserFinder {
  User findUser(String email);

  boolean doesUserExist(String email);

  User findUserByTransactionId(TransactionId transactionId);

  Collection<User> findUsersWithRole(Role role);
}
