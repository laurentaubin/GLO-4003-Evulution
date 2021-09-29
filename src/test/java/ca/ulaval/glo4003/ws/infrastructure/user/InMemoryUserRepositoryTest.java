package ca.ulaval.glo4003.ws.infrastructure.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {
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
}
