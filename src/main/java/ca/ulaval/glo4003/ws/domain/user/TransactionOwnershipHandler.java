package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;

public class TransactionOwnershipHandler {
  private final UserRepository userRepository;

  public TransactionOwnershipHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void addTransactionOwnership(Session session, TransactionId transactionId) {
    User user = userRepository.findUser(session.getEmail());
    user.addTransaction(transactionId);
    userRepository.update(user);
  }

  public void validateOwnership(Session session, TransactionId transactionId) {
    User user = userRepository.findUser(session.getEmail());

    if (!user.doesOwnTransaction(transactionId)) {
      throw new WrongOwnerException();
    }
  }
}
