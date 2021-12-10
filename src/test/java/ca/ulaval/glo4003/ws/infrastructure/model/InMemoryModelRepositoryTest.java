package ca.ulaval.glo4003.ws.infrastructure.model;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.ModelNotFoundException;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InMemoryModelRepositoryTest {
  private static final String A_MODEL_NAME = "A MODEL NAME";
  private static final String INVALID_MODEL_NAME = "invalid model name";

  @Mock private ModelDto aModelDto;
  @Mock private Model aModel;
  @Mock private ModelAssembler modelAssembler;

  private InMemoryModelRepository repository;

  @BeforeEach
  public void setUpRepository() {
    Map<String, ModelDto> models = new HashMap<>();
    models.put(A_MODEL_NAME, aModelDto);
    repository = new InMemoryModelRepository(models, modelAssembler);
  }

  @Test
  public void whenFindByModelName_thenReturnModel() {
    // given
    given(modelAssembler.assembleModel(aModelDto)).willReturn(aModel);

    // when
    Model model = repository.findByModel(A_MODEL_NAME);

    // then
    assertThat(model).isEqualTo(aModel);
  }

  @Test
  public void givenANonExistingModel_whenFindModelByName_thenThrowModelNotFoundException() {
    // when
    Executable findingModel = () -> repository.findByModel(INVALID_MODEL_NAME);

    // then
    assertThrows(ModelNotFoundException.class, findingModel);
  }

  @Test
  public void givenModels_whenFindAllModels_thenReturnAllModels() {
    // given
    Collection<Model> expectedModels = new ArrayList<>();
    expectedModels.add(aModel);
    given(modelAssembler.assembleModel(aModelDto)).willReturn(aModel);

    // when
    Collection<Model> models = repository.findAllModels();

    // then
    assertThat(models).containsExactlyElementsIn(expectedModels);
  }
}
