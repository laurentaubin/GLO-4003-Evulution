package ca.ulaval.glo4003.ws.infrastructure.model;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

class ModelAssemblerTest {
  private static final String A_NAME = "a name";
  private static final String A_STYLE = "a style";
  private static final BigDecimal AN_EFFICIENCY = BigDecimal.valueOf(432546);
  private static final int A_PRICE = 43;
  private static final int A_PRODUCTION_TIME = 645;

  private ModelAssembler assembler;

  @BeforeEach
  public void setUpAssembler() {
    assembler = new ModelAssembler();
  }

  @Test
  public void givenAModelDto_whenAssembleModel_thenModelIsAssembled() {
    // given
    ModelDto modelDto = new ModelDto();
    modelDto.name = A_NAME;
    modelDto.basePrice = A_PRICE;
    modelDto.efficiencyEquivalenceRate = AN_EFFICIENCY;
    modelDto.productionTime = A_PRODUCTION_TIME;
    modelDto.style = A_STYLE;

    // when
    Model model = assembler.assembleModel(modelDto);

    // then
    assertThat(model.getName()).isEqualTo(A_NAME.toUpperCase());
    assertThat(model.getEfficiency()).isEqualTo(AN_EFFICIENCY);
    assertThat(model.getPrice()).isEqualTo(A_PRICE);
    assertThat(model.getProductionTime()).isEqualTo(new ProductionTime(A_PRODUCTION_TIME));
    assertThat(model.getStyle()).isEqualTo(A_STYLE);
  }
}
