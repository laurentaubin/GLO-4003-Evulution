package ca.ulaval.glo4003.ws.infrastructure.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.testUtil.UserBuilder;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDtoAssemblerTest {

  private UserDtoAssembler assembler;

  @BeforeEach
  public void setUp() {
    assembler = new UserDtoAssembler();
  }

  @Test
  public void givenUser_whenAssemble_thenReturnUserDtoWithSameAttributes() {
    // given
    User user = new UserBuilder().build();

    // when
    UserDto userDto = assembler.assemble(user);

    // then
    assertThat(userDto.getName()).isEqualTo(user.getName());
    assertThat(userDto.getBirthDate()).isEqualTo(user.getBirthDate());
    assertThat(userDto.getSex()).isEqualTo(user.getSex());
    assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    assertThat(userDto.getPassword()).isEqualTo(user.getPassword());
    assertThat(userDto.getRoles()).containsExactly(user.getRoles().toArray());
    assertThat(userDto.getTransactions()).containsExactly(user.getTransactions().toArray());
    assertThat(userDto.getDeliveries()).containsExactly(user.getDeliveries().toArray());
  }

  @Test
  public void givenAssembledUserDto_whenAddingRoleToUser_thenUserDtoIsNotAffected() {
    // given
    User user = new UserBuilder().build();
    UserDto userDto = assembler.assemble(user);

    // when
    user.addRole(Role.ADMIN);

    // then
    assertThat(userDto.getRoles()).doesNotContain(Role.ADMIN);
  }

  @Test
  public void givenAssembledUserDto_whenAddingTransactionToUser_thenUserDtoIsNotAffected() {
    // given
    User user = new UserBuilder().build();
    UserDto userDto = assembler.assemble(user);
    TransactionId transactionId = new TransactionId("odsakdaos");

    // when
    user.addTransaction(transactionId);

    // then
    assertThat(userDto.getTransactions()).doesNotContain(transactionId);
  }

  @Test
  public void givenAssembledUserDto_whenAddingDeliveryToUser_thenUserDtoIsNotAffected() {
    // given
    User user = new UserBuilder().build();
    UserDto userDto = assembler.assemble(user);
    DeliveryId deliveryid = new DeliveryId("odsakdoa");

    // when
    user.addDelivery(deliveryid);

    // then
    assertThat(userDto.getDeliveries()).doesNotContain(deliveryid);
  }

  @Test
  public void givenUserDto_whenAssemble_thenReturnUserWithSameAttributes() {
    // given
    UserDto userDto = givenUserDto();

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.getName()).isEqualTo(userDto.getName());
    assertThat(user.getBirthDate()).isEqualTo(userDto.getBirthDate());
    assertThat(user.getSex()).isEqualTo(userDto.getSex());
    assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    assertThat(user.getPassword()).isEqualTo(userDto.getPassword());
  }

  @Test
  public void givenUserDtoWithBaseAndAdminRole_whenAssemble_thenReturnUserWithSameRoles() {
    // given
    UserDto userDto = givenUserDtoWithRoles(Set.of(Role.BASE, Role.ADMIN));

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.getRoles()).contains(Role.BASE);
    assertThat(user.getRoles()).contains(Role.ADMIN);
  }

  @Test
  public void givenUserDtoWithTransactions_whenAssemble_thenReturnUserWithSameTransactions() {
    // given
    TransactionId transactionId = new TransactionId("doaskdsoa");
    UserDto userDto = givenUserDtoWithTransactionIds(List.of(transactionId));

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.doesOwnTransaction(transactionId)).isTrue();
  }

  @Test
  public void givenUserDtoWithDeliveries_whenAssemble_thenReturnUserWithSameDeliveries() {
    // given
    DeliveryId deliveryId = new DeliveryId("doaskdsoa");
    UserDto userDto = givenUserDtoWithDeliveryIds(List.of(deliveryId));

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.doesOwnDelivery(deliveryId)).isTrue();
  }

  private UserDto givenUserDto() {
    return givenUserDtoWithRolesTransactionIdsAndDeliveryIds(
        Collections.emptySet(), Collections.emptyList(), Collections.emptyList());
  }

  private UserDto givenUserDtoWithRoles(Set<Role> roles) {
    return givenUserDtoWithRolesTransactionIdsAndDeliveryIds(
        roles, Collections.emptyList(), Collections.emptyList());
  }

  private UserDto givenUserDtoWithTransactionIds(Collection<TransactionId> transactionIds) {
    return givenUserDtoWithRolesTransactionIdsAndDeliveryIds(
        Collections.emptySet(), transactionIds, Collections.emptyList());
  }

  private UserDto givenUserDtoWithDeliveryIds(Collection<DeliveryId> deliveryIds) {
    return givenUserDtoWithRolesTransactionIdsAndDeliveryIds(
        Collections.emptySet(), Collections.emptyList(), deliveryIds);
  }

  private UserDto givenUserDtoWithRolesTransactionIdsAndDeliveryIds(
      Set<Role> roles,
      Collection<TransactionId> transactionIds,
      Collection<DeliveryId> deliveryIds) {
    return new UserDto(
        "aName",
        new BirthDate(LocalDate.now()),
        "aSex",
        "anEmail",
        "aPassword",
        roles,
        transactionIds,
        deliveryIds);
  }
}
