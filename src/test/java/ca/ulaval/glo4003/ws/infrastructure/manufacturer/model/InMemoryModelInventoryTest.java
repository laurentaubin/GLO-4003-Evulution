package ca.ulaval.glo4003.ws.infrastructure.manufacturer.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.warehouse.model.NotAvailableModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class InMemoryModelInventoryTest {
  private static final String A_MODEL_TYPE = "aModel";

  private InMemoryModelInventory modelInventory;

  @BeforeEach
  public void setUp() {
    modelInventory = new InMemoryModelInventory();
  }

  @Test
  public void givenModelNotInStock_whenIsInStock_thenReturnFalse() {
    // when
    boolean isInStock = modelInventory.isInStock(A_MODEL_TYPE);

    // then
    assertThat(isInStock).isFalse();
  }

  @Test
  public void givenModelInStock_whenIsInStock_thenReturnTrue() {
    // given
    modelInventory.addOne(A_MODEL_TYPE);

    // when
    boolean isInStock = modelInventory.isInStock(A_MODEL_TYPE);

    // then
    assertThat(isInStock).isTrue();
  }

  @Test
  public void givenModelNotInStock_whenRemoveOne_thenThrowNotAvailableModelException() {
    // when
    Executable removingModelFromInventory = () -> modelInventory.removeOne(A_MODEL_TYPE);

    // then
    assertThrows(NotAvailableModelException.class, removingModelFromInventory);
  }

  @Test
  public void givenManyModelInStock_whenRemoveOne_thenModelIsStillInStock() {
    // given
    modelInventory.addOne(A_MODEL_TYPE);
    modelInventory.addOne(A_MODEL_TYPE);

    // when
    modelInventory.removeOne(A_MODEL_TYPE);

    // then
    assertThat(modelInventory.isInStock(A_MODEL_TYPE)).isTrue();
  }

  @Test
  public void givenOneModelInStock_whenRemove_thenModelIsNotInStockAnymore() {
    // given
    modelInventory.addOne(A_MODEL_TYPE);

    // when
    modelInventory.removeOne(A_MODEL_TYPE);

    // then
    assertThat(modelInventory.isInStock(A_MODEL_TYPE)).isFalse();
  }
}
