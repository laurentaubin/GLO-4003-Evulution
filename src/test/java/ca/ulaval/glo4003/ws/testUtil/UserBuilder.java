package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserBuilder {
  private String name = "a name";
  private BirthDate birthDate = new BirthDate(LocalDate.of(4584, 7, 18));
  private String email = "an@email.com";
  private String password = "a password";
  private String sex = "a sex";
  private List<Role> roles = new ArrayList<>(List.of(Role.BASE));

  public UserBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public UserBuilder withBirthDate(BirthDate birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public UserBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public UserBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public UserBuilder withSex(String sex) {
    this.sex = sex;
    return this;
  }

  public UserBuilder withRoles(List<Role> roles) {
    this.roles = roles;
    return this;
  }

  public User build() {
    User user = new User(name, birthDate, sex, email, password);

    for (Role role : roles) {
      user.addRole(role);
    }

    return user;
  }
}
