package ca.ulaval.glo4003.ws.infrastructure.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.fixture.UserBuilder;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDtoAssemblerTest {
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("xyz");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("abc");

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
    assertThat(userDto.getRoles()).containsExactly(user.getRoles().toArray());
    assertThat(userDto.getTransactionDeliveries()).isEqualTo(user.getTransactionIdToDeliveryId());
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

    // when
    user.addTransactionDelivery(A_TRANSACTION_ID, null);

    // then
    assertThat(userDto.getTransactionDeliveries()).doesNotContainKey(A_TRANSACTION_ID);
  }

  @Test
  public void givenAssembledUserDto_whenAddingDeliveryToUser_thenUserDtoIsNotAffected() {
    // given
    User user = new UserBuilder().build();
    UserDto userDto = assembler.assemble(user);

    // when
    user.addTransactionDelivery(A_TRANSACTION_ID, A_DELIVERY_ID);

    // then
    assertThat(userDto.getTransactionDeliveries())
        .doesNotContainEntry(A_TRANSACTION_ID, A_DELIVERY_ID);
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
  public void givenUserDtoWithTransactions_whenAssemble_thenReturnUserWithSameTransaction() {
    // given
    Map<TransactionId, DeliveryId> transactionDeliveries = createTransactionDeliveries();
    UserDto userDto = givenUserDtoWithTransactionDeliveries(transactionDeliveries);

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.ownsTransaction(A_TRANSACTION_ID)).isTrue();
  }

  @Test
  public void
      givenTransactionsAndDeliveries_whenAssemble_thenReturnUserWithSameTransactionDeliveries() {
    // given
    Map<TransactionId, DeliveryId> transactionDeliveries = createTransactionDeliveries();
    UserDto userDto = givenUserDtoWithTransactionDeliveries(transactionDeliveries);

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.getTransactionIdToDeliveryId()).isEqualTo(userDto.getTransactionDeliveries());
  }

  @Test
  public void givenUserDtoWithDeliveries_whenAssemble_thenReturnUserWithSameDeliveries() {
    // given
    Map<TransactionId, DeliveryId> transactionDeliveries = createTransactionDeliveries();
    UserDto userDto = givenUserDtoWithTransactionDeliveries(transactionDeliveries);

    // when
    User user = assembler.assemble(userDto);

    // then
    assertThat(user.ownDelivery(A_DELIVERY_ID)).isTrue();
  }

  private UserDto givenUserDto() {
    return givenUserDtoWithRolesAndTransactionDeliveries(
        Collections.emptySet(), createTransactionDeliveries());
  }

  private UserDto givenUserDtoWithRoles(Set<Role> roles) {
    return givenUserDtoWithRolesAndTransactionDeliveries(roles, new HashMap<>());
  }

  private UserDto givenUserDtoWithTransactionDeliveries(
      Map<TransactionId, DeliveryId> transactionDeliveries) {
    return givenUserDtoWithRolesAndTransactionDeliveries(
        Collections.emptySet(), transactionDeliveries);
  }

  private UserDto givenUserDtoWithRolesAndTransactionDeliveries(
      Set<Role> roles, Map<TransactionId, DeliveryId> transactionDeliveries) {
    return new UserDto(
        "aName", new BirthDate(LocalDate.now()), "aSex", "anEmail", roles, transactionDeliveries);
  }

  private Map<TransactionId, DeliveryId> createTransactionDeliveries() {
    Map<TransactionId, DeliveryId> transactionDeliveries = new HashMap<>();
    transactionDeliveries.put(A_TRANSACTION_ID, A_DELIVERY_ID);
    return transactionDeliveries;
  }
}
