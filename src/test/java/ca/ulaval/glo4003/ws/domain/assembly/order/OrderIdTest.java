package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrderIdTest {
  private static final String AN_ID = "hg7e2d";
  private static final String ANOTHER_ID = "ijicip32nti7";

  @Test
  public void givenTwoOrderIdsWithSameId_whenEquals_thenReturnTrue() {
    // given
    OrderId anOrderId = new OrderId(AN_ID);
    OrderId anotherOrderId = new OrderId(AN_ID);

    // when
    boolean areEquals = anOrderId.equals(anotherOrderId);

    // then
    assertTrue(areEquals);
  }

  @Test
  public void givenTwoOrderIdsWithDifferentId_whenEquals_thenReturnFalse() {
    // given
    OrderId anOrderId = new OrderId(AN_ID);
    OrderId anotherOrderId = new OrderId(ANOTHER_ID);

    // when
    boolean areEquals = anOrderId.equals(anotherOrderId);

    // then
    assertFalse(areEquals);
  }

  @Test
  public void givenAnOrder_whenHashCode_thenIsNotNull() {
    // given
    OrderId anOrderId = new OrderId(AN_ID);

    // when
    int hashCode = anOrderId.hashCode();

    // then
    assertThat(hashCode).isNotNull();
  }
}
