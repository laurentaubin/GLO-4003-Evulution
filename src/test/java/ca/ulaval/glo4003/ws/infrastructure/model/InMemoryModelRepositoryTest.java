package ca.ulaval.glo4003.ws.infrastructure.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.ModelNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryModelRepositoryTest {
  private static final String A_MODEL_NAME = "A MODEL NAME";
  private static final String AN_INVALID_MODEL_NAME = "invalid model name";

  @Mock private ModelDto modelDto;
  @Mock private Model model;
  @Mock private ModelAssembler modelAssembler;

  private InMemoryModelRepository repository;

  @BeforeEach
  public void setUpRepository() {
    Map<String, ModelDto> models = new HashMap<>();
    models.put(A_MODEL_NAME, modelDto);
    repository = new InMemoryModelRepository(models, modelAssembler);
  }

  @Test
  public void whenFindByModelName_thenReturnModel() {
    // given
    given(modelAssembler.assembleModel(modelDto)).willReturn(model);

    // when
    Model model = repository.findByModel(A_MODEL_NAME);

    // then
    assertThat(model).isEqualTo(this.model);
  }

  @Test
  public void givenANonExistingModel_whenFindModelByName_thenThrowModelNotFoundException() {
    // when
    Executable findingModel = () -> repository.findByModel(AN_INVALID_MODEL_NAME);

    // then
    assertThrows(ModelNotFoundException.class, findingModel);
  }

  @Test
  public void givenModels_whenFindAllModels_thenReturnAllModels() {
    // given
    Collection<Model> expectedModels = new ArrayList<>();
    expectedModels.add(model);
    given(modelAssembler.assembleModel(modelDto)).willReturn(model);

    // when
    Collection<Model> models = repository.findAllModels();

    // then
    assertThat(models).containsExactlyElementsIn(expectedModels);
  }
}
