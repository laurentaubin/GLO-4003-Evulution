package ca.ulaval.glo4003.ws.infrastructure.user;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class InMemoryUserRepositoryTest {
  private static final String AN_EMAIL = "remw@mfs.com";
  private static final String ANOTHER_NAME = "sdaidhsauidhasiuhda";

  private InMemoryUserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = new InMemoryUserRepository();
  }

  @Test
  void whenRegisterUser_thenUserIsStored() {
    // given
    User aUser = new UserBuilder().build();

    // when
    userRepository.registerUser(aUser);
    User actualUser = userRepository.findUser(aUser.getEmail());

    // then
    assertThat(actualUser).isEqualTo(aUser);
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
}
