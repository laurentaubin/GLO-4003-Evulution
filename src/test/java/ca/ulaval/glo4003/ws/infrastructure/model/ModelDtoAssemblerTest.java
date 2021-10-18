package ca.ulaval.glo4003.ws.infrastructure.model;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelDtoAssemblerTest {
  private static final String A_NAME = "a name";
  private static final String A_STYLE = "a style";
  private static final BigDecimal AN_EFFICIENCY = BigDecimal.valueOf(432546);
  private static final int A_PRICE = 43;
  private static final int A_TIME_TO_PRODUCE = 645;

  private ModelDtoAssembler assembler;

  @BeforeEach
  public void setUpAssembler() {
    assembler = new ModelDtoAssembler();
  }

  @Test
  public void givenAModelDto_whenAssembleModels_thenModelIsAssembled() {
    // given
    ModelDto modelDto = new ModelDto();
    modelDto.name = A_NAME;
    modelDto.basePrice = A_PRICE;
    modelDto.efficiencyEquivalenceRate = AN_EFFICIENCY;
    modelDto.timeToProduce = A_TIME_TO_PRODUCE;
    modelDto.style = A_STYLE;

    // when
    List<Model> models = assembler.assembleModels(List.of(modelDto));

    // then
    Model assembledModel = models.get(0);
    assertThat(assembledModel.getName()).isEqualTo(A_NAME.toUpperCase());
    assertThat(assembledModel.getEfficiency()).isEqualTo(AN_EFFICIENCY);
    assertThat(assembledModel.getBasePrice()).isEqualTo(A_PRICE);
    assertThat(assembledModel.getTimeToProduce()).isEqualTo(A_TIME_TO_PRODUCE);
    assertThat(assembledModel.getStyle()).isEqualTo(A_STYLE);
  }
}
