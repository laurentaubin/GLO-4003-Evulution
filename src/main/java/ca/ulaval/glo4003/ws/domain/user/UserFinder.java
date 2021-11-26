package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public interface UserFinder {
    User findUser(String email);

    boolean doesUserExist(String email);

    User findUserByTransactionId(TransactionId transactionId);
}
