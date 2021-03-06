package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository, UserFinder {
  private final Map<String, UserDto> users = new HashMap<>();
  private final UserDtoAssembler userDtoAssembler;

  public InMemoryUserRepository() {
    this(new UserDtoAssembler());
  }

  public InMemoryUserRepository(UserDtoAssembler userDtoAssembler) {
    this.userDtoAssembler = userDtoAssembler;
  }

  @Override
  public void registerUser(User user) {
    UserDto userDto = userDtoAssembler.assemble(user);
    users.put(user.getEmail(), userDto);
  }

  @Override
  public User findUser(String email) {
    if (users.containsKey(email)) {
      UserDto userDto = users.get(email);
      return userDtoAssembler.assemble(userDto);
    }
    throw new UserNotFoundException();
  }

  @Override
  public void update(User user) {
    UserDto userDto = userDtoAssembler.assemble(user);
    users.put(user.getEmail(), userDto);
  }

  @Override
  public boolean doesUserExist(String email) {
    return users.containsKey(email);
  }

  @Override
  public User findUserByTransactionId(TransactionId transactionId) {
    List<User> mappedUsers =
        users.values().stream().map(userDtoAssembler::assemble).collect(Collectors.toList());
    return mappedUsers.stream()
        .filter(user -> user.ownsTransaction(transactionId))
        .findFirst()
        .get();
  }

  @Override
  public List<User> findUsersWithRole(Role role) {
    List<User> mappedUsers =
        users.values().stream().map(userDtoAssembler::assemble).collect(Collectors.toList());
    return mappedUsers.stream()
        .filter(user -> user.getRoles().contains(role))
        .collect(Collectors.toList());
  }

  @Override
  public List<User> findAll() {
    return users.values().stream().map(userDtoAssembler::assemble).collect(Collectors.toList());
  }
}
