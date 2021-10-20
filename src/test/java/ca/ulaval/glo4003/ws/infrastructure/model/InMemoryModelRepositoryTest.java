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

@ExtendWith(MockitoExtension.class)
class InMemoryModelRepositoryTest {
  private static final String A_MODEL_NAME = "A MODEL NAME";
  private static final String INVALID_MODEL_NAME = "invalid model name";

  @Mock private Model aModel;

  private InMemoryModelRepository repository;

  @BeforeEach
  public void setUpRepository() {
    Map<String, Model> models = new HashMap<>();
    models.put(A_MODEL_NAME, aModel);
    repository = new InMemoryModelRepository(models);
  }

  @Test
  public void whenFindByModelName_thenReturnModel() {
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

    // when
    Collection<Model> models = repository.findAllModels();

    // then
    assertThat(models).containsExactlyElementsIn(expectedModels);
  }
}
