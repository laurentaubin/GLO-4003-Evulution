package ca.ulaval.glo4003.ws.domain.warehouse.model;

import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ModelOrderFactoryTest {
  private final String A_MODEL_TYPE = "a model";
  private final AssemblyTime AN_ASSEMBLY_TIME = new AssemblyTime(1);

  private ModelOrderFactory modelOrderFactory;

  @BeforeEach
  void setUp() {
    this.modelOrderFactory = new ModelOrderFactory();
  }

  @Test
  public void givenAModelType_whenCreate_ThenReturnModelOrderWithGivenModelType() {
    // when
    ModelOrder modelOrder = modelOrderFactory.create(A_MODEL_TYPE, AN_ASSEMBLY_TIME);

    // then
    assertThat(modelOrder.getModelType()).isEqualTo(A_MODEL_TYPE);
  }
}
