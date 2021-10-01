package ca.ulaval.glo4003.ws.infrastructure.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {
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
    User actualUser = userRepository.findUser(aUser.getEmail()).get();

    // then
    assertThat(actualUser).isEqualTo(aUser);
  }

  @Test
  public void givenUserNotPresent_whenFindUser_thenReturnEmptyOptional() {
    // when
    Optional<User> noUser = userRepository.findUser("a random email");

    // then
    assertThat(noUser.isEmpty()).isTrue();
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
    User updatedUser = userRepository.findUser(anotherUser.getEmail()).get();
    assertThat(updatedUser.getName()).matches(ANOTHER_NAME);
  }
}
