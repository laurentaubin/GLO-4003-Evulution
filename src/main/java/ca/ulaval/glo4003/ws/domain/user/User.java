package ca.ulaval.glo4003.ws.domain.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
  private final String name;
  private final BirthDate birthDate;
  private final String sex;
  private final String email;
  private final String password;
  private final Set<Role> roles;

  public User(String name, BirthDate birthDate, String sex, String email, String password) {
    this.name = name;
    this.birthDate = birthDate;
    this.sex = sex;
    this.email = email;
    this.password = password;
    this.roles = new HashSet<>(List.of(Role.BASE));
  }

  public String getName() {
    return name;
  }

  public BirthDate getBirthDate() {
    return birthDate;
  }

  public String getSex() {
    return sex;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void addRole(Role role) {
    this.roles.add(role);
  }

  public Set<Role> getRoles() {
    return this.roles;
  }

  public boolean isAllowed(List<Role> requestedRoles) {
    return !Collections.disjoint(roles, requestedRoles);
  }
}
