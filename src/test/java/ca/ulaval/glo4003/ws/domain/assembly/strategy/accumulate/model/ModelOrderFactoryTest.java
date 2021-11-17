package ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelOrderFactoryTest {
  private final String A_MODEL_TYPE = "a model";
  private ModelOrderFactory modelOrderFactory;

  @BeforeEach
  void setUp() {
    this.modelOrderFactory = new ModelOrderFactory();
  }

  @Test
  public void givenAModelType_whenCreate_ThenReturnModelOrderWithGivenModelType() {
    // when
    ModelOrder modelOrder = modelOrderFactory.create(A_MODEL_TYPE);

    // then
    assertThat(modelOrder.getModelType()).isEqualTo(A_MODEL_TYPE);
  }
}
