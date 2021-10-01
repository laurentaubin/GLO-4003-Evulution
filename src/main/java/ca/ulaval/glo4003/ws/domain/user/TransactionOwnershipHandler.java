package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.exception.WrongOwnerException;

public class TransactionOwnershipHandler {
  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;

  public TransactionOwnershipHandler(
      SessionRepository sessionRepository, UserRepository userRepository) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
  }

  public void addTransactionOwnership(Session session, TransactionId transactionId) {
    User user = userRepository.findUser(session.getEmail()).get();
    user.addTransaction(transactionId);
    userRepository.update(user);
  }

  public void validateOwnership(Session session, TransactionId transactionId) {
    User user = userRepository.findUser(session.getEmail()).get();

    if (!user.doesOwnTransaction(transactionId)) {
      throw new WrongOwnerException();
    }
  }
}
