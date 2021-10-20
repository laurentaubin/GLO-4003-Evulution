package ca.ulaval.glo4003.ws.infrastructure.model;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class ModelDtoAssemblerTest {
  private static final String A_NAME = "a name";
  private static final String A_STYLE = "a style";
  private static final BigDecimal AN_EFFICIENCY = BigDecimal.valueOf(432546);
  private static final int A_PRICE = 43;
  private static final int A_PRODUCTION_TIME = 645;

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
    modelDto.productionTime = A_PRODUCTION_TIME;
    modelDto.style = A_STYLE;

    // when
    List<Model> models = assembler.assembleModels(List.of(modelDto));

    // then
    Model assembledModel = models.get(0);
    assertThat(assembledModel.getName()).isEqualTo(A_NAME.toUpperCase());
    assertThat(assembledModel.getEfficiency()).isEqualTo(AN_EFFICIENCY);
    assertThat(assembledModel.getBasePrice()).isEqualTo(A_PRICE);
    assertThat(assembledModel.getProductionTime()).isEqualTo(new ProductionTime(A_PRODUCTION_TIME));
    assertThat(assembledModel.getStyle()).isEqualTo(A_STYLE);
  }
}