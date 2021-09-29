package ca.ulaval.glo4003.ws.domain.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {
  private static final Role A_ROLE = Role.ADMIN;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new UserBuilder().build();
  }

  @Test
  public void whenCreate_thenUserOnlyHasBaseRole() {
    assertThat(user.getRoles()).contains(Role.BASE);
    assertThat(user.getRoles()).hasSize(1);
  }

  @Test
  public void givenARole_whenAddRole_thenUserHasNewRole() {
    user.addRole(A_ROLE);

    assertThat(user.getRoles()).contains(A_ROLE);
  }

  @Test
  public void givenUserPossessRequestedRole_whenIsAllowed_thenUserIsAllowed() {
    User userWithRequestedRoles = new UserBuilder().withRoles(List.of(Role.BASE)).build();
    List<Role> requestedRoles = List.of(Role.BASE, Role.ADMIN);

    boolean isAllowed = userWithRequestedRoles.isAllowed(requestedRoles);

    assertThat(isAllowed).isTrue();
  }

  @Test
  public void givenUserWithoutRequestedRole_whenIsAllowed_thenUserIsNotAllowed() {
    User userWithRequestedRoles = new UserBuilder().withRoles(List.of(Role.BASE)).build();
    List<Role> requestedRoles = List.of(Role.ADMIN);

    boolean isAllowed = userWithRequestedRoles.isAllowed(requestedRoles);

    assertThat(isAllowed).isFalse();
  }
}
