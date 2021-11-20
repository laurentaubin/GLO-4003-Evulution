package ca.ulaval.glo4003.ws.infrastructure.user;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InMemoryUserRepositoryTest {
  private static final TransactionId A_TRANSACTION_ID = TransactionId.fromString("id");
  private static final String AN_EMAIL = "remw@mfs.com";
  private static final String ANOTHER_EMAIL = "email2@email.com";
  private static final String ANOTHER_NAME = "sdaidhsauidhasiuhda";

  private final UserDtoAssembler userDtoAssembler = new UserDtoAssembler();
  @Mock private User user;

  private InMemoryUserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = new InMemoryUserRepository(userDtoAssembler);
  }

  @Test
  void whenRegisterUser_thenUserIsStored() {
    // given
    User aUser = new UserBuilder().build();

    // when
    userRepository.registerUser(aUser);
    User actualUser = userRepository.findUser(aUser.getEmail());

    // then
    assertThat(actualUser.getEmail()).isEqualTo(aUser.getEmail());
    assertThat(actualUser.getPassword()).isEqualTo(aUser.getPassword());
  }

  @Test
  public void givenUserNotPresent_whenFindUser_thenReturnEmptyOptional() {
    // when
    Executable findingUser = () -> userRepository.findUser("a random email");

    // then
    assertThrows(UserNotFoundException.class, findingUser);
  }

  @Test
  public void givenUserExists_whenUpdate_thenUpdateUser() {
    // given
    User aUser = new UserBuilder().build();
    userRepository.registerUser(aUser);
    User anotherUser = new UserBuilder().withName(ANOTHER_NAME).build();

    // when
    userRepository.update(anotherUser);

    // then
    User updatedUser = userRepository.findUser(anotherUser.getEmail());
    assertThat(updatedUser.getName()).matches(ANOTHER_NAME);
  }

  @Test
  public void givenUserExists_whenDoesUserExist_thenReturnTrue() {
    // given
    User aUser = new UserBuilder().withEmail(AN_EMAIL).build();
    userRepository.registerUser(aUser);

    // when
    boolean doesUserExist = userRepository.doesUserExist(AN_EMAIL);

    // then
    assertThat(doesUserExist).isTrue();
  }

  @Test
  public void givenDoesNotUserExist_whenDoesUserExist_thenReturnFalse() {
    // when
    boolean doesUserExist = userRepository.doesUserExist(AN_EMAIL);

    // then
    assertThat(doesUserExist).isFalse();
  }

  @Test
  public void givenUserSaved_whenAddRoleToOriginalUser_thenSavedUserIsNotUpdated() {
    // given
    User user = new UserBuilder().withRoles(List.of(Role.BASE)).build();
    userRepository.registerUser(user);

    // when
    user.addRole(Role.ADMIN);
    User originalUser = userRepository.findUser(user.getEmail());

    // then
    assertThat(originalUser.getRoles()).isNotEqualTo(user.getRoles());
  }

  @Test
  public void givenTransactionId_whenFindByTransaction_thenReturnUser() {
    // given
    given(user.getEmail()).willReturn(AN_EMAIL);
    Map<TransactionId, DeliveryId> transactionDeliveries = new HashMap<>();
    transactionDeliveries.put(A_TRANSACTION_ID, null);
    given(user.getTransactionIdToDeliveryId()).willReturn(transactionDeliveries);
    userRepository.registerUser(user);

    // when
    User foundUser = userRepository.findUserByTransactionId(A_TRANSACTION_ID);

    // then
    assertThat(foundUser.getEmail()).isEqualTo(AN_EMAIL);
  }

  @Test
  public void givenUsersSaved_whenFindAll_thenReturnAllUsers() {
    // given
    User aUser = new UserBuilder().withEmail(AN_EMAIL).build();
    User anotherUser = new UserBuilder().withEmail(ANOTHER_EMAIL).build();
    userRepository.registerUser(aUser);
    userRepository.registerUser(anotherUser);

    // when
    List<User> foundUsers = userRepository.findAll();

    // then
    assertThat(foundUsers).hasSize(2);
  }
}
