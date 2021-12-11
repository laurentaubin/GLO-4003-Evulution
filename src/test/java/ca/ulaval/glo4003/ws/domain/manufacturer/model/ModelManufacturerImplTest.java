package ca.ulaval.glo4003.ws.domain.manufacturer.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.exception.InvalidModelQuantityInQueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelManufacturerImplTest {
  private static final int THREE_WEEKS = 3;
  private static final int MANY_WEEKS = 43;
  private static final OrderId AN_ID = new OrderId("id");
  private static final AssemblyTime AN_ASSEMBLY_TIME = new AssemblyTime(5);
  private static final String A_MODEL_TYPE = "modelType";
  private static final ModelOrder A_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(AN_ID)
          .withAssemblyTime(AN_ASSEMBLY_TIME)
          .withModelName(A_MODEL_TYPE)
          .build();
  private static final OrderId ANOTHER_ID = new OrderId("id");
  private static final AssemblyTime ANOTHER_ASSEMBLY_TIME = new AssemblyTime(10);
  private static final String ANOTHER_MODEL_TYPE = "anotherModelType";
  private static final ModelOrder ANOTHER_MODEL_ORDER =
      new ModelOrderBuilder()
          .withModelName(ANOTHER_MODEL_TYPE)
          .withOrderId(ANOTHER_ID)
          .withAssemblyTime(ANOTHER_ASSEMBLY_TIME)
          .build();

  @Mock private ModelAssemblyLineAdapter modelAssemblyLineAdapter;
  @Mock private ModelAssembledObserver modelAssembledObserver;

  private ModelManufacturerImpl modelManufacturer;

  @BeforeEach
  public void setUp() {
    modelManufacturer = new ModelManufacturerImpl(modelAssemblyLineAdapter);
    modelManufacturer.register(modelAssembledObserver);
  }

  @Test
  public void givenAModelOrderAssembled_whenAdvance_thenNotifyObserversModelOrderIsAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    modelManufacturer.advanceTime();

    // then
    verify(modelAssembledObserver).listenToModelAssembled(A_MODEL_ORDER);
  }

  @Test
  public void givenModelOrderInProgress_whenAdvance_thenObserversAreNotNotifyOfAnything() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.IN_PROGRESS);

    // when
    modelManufacturer.advanceTime();

    // then
    verify(modelAssembledObserver, never()).listenToModelAssembled(A_MODEL_ORDER);
  }

  @Test
  public void givenManyModelOrdersMade_whenAdvance_thenOnlyTheFirstOneIsSentToBeAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(ANOTHER_MODEL_ORDER);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    modelManufacturer.advanceTime();

    // then
    verify(modelAssembledObserver).listenToModelAssembled(A_MODEL_ORDER);
    verify(modelAssembledObserver, never()).listenToModelAssembled(ANOTHER_MODEL_ORDER);
  }

  @Test
  public void
      givenManyModelOrdersMadeAndFirstOneIsAssembled_whenAdvance_thenSecondOrderIsSentToBeAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(ANOTHER_MODEL_ORDER);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    modelManufacturer.advanceTime();

    // then
    verify(modelAssemblyLineAdapter).addOrder(ANOTHER_MODEL_ORDER);
  }

  @Test
  public void givenAModelOrderAssembled_whenAdvanceManyTimes_thenNotifyOnlyOnceAndDoNothingAfter() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    advanceMultipleTimes(MANY_WEEKS);

    // then
    assertThat(mockingDetails(modelAssembledObserver).getInvocations().size()).isEqualTo(1);
  }

  @Test
  public void
      givenADesiredModelTypeJustReceivedForAssembly_whenComputeRemainingTimeToProduceNextModelType_thenReturnRemainingAssemblyTimeOfCurrentModelOrderBeingAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeRemainingTimeToProduceNextModelType(A_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime).isEqualTo(A_MODEL_ORDER.getAssemblyTime());
  }

  @Test
  public void
      givenADesiredModelTypeIsInProgressAfterSomeTime_whenComputeRemainingTimeToProduceNextModelType_thenReturnRemainingAssemblyTimeOfCurrentModelOrderBeingAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    advanceMultipleTimes(THREE_WEEKS);
    AssemblyTime expectedAssemblyTime = AN_ASSEMBLY_TIME.subtract(new AssemblyTime(THREE_WEEKS));

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeRemainingTimeToProduceNextModelType(A_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenTwoDifferentOrderTypesAndAskingForSecondType_whenComputeRemainingTimeToProduceNextModelType_thenComputesTimeForTheSecondOrder() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(ANOTHER_MODEL_ORDER);
    AssemblyTime expectedAssemblyTime =
        ANOTHER_MODEL_ORDER.getAssemblyTime().add(A_MODEL_ORDER.getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeRemainingTimeToProduceNextModelType(
            ANOTHER_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedAssemblyTime.inWeeks());
  }

  @Test
  public void
      givenADesiredModelTypeJustReceivedForAssembly_whenComputeTimeToProduceQuantityOfModel_thenReturnRemainingAssemblyTimeOfCurrentModelOrderBeingAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeTimeToProduceQuantityOfModel(1, A_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime).isEqualTo(A_MODEL_ORDER.getAssemblyTime());
  }

  @Test
  public void
      givenADesiredModelTypeIsInProgressAfterSomeTime_whenComputeTimeToProduceOneOfModel_thenReturnRemainingAssemblyTimeOfCurrentModelOrderBeingAssembled() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    advanceMultipleTimes(THREE_WEEKS);
    AssemblyTime expectedAssemblyTime = AN_ASSEMBLY_TIME.subtract(new AssemblyTime(THREE_WEEKS));

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeTimeToProduceQuantityOfModel(1, A_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenTwoOrdersWithDifferentTypes_whenComputeTimeToProduceOneOfSecondModel_thenReturnTimeToProduceBothModels() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(ANOTHER_MODEL_ORDER);
    AssemblyTime expectedAssemblyTime =
        ANOTHER_MODEL_ORDER.getAssemblyTime().add(A_MODEL_ORDER.getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeTimeToProduceQuantityOfModel(
            1, ANOTHER_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedAssemblyTime.inWeeks());
  }

  @Test
  public void
      givenTwoOrdersWithSameType_whenComputeTimeToProduceTwoOfModel_thenReturnTimeToProduceBothModels() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(A_MODEL_ORDER);
    AssemblyTime expectedAssemblyTime =
        A_MODEL_ORDER.getAssemblyTime().add(A_MODEL_ORDER.getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeTimeToProduceQuantityOfModel(2, A_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedAssemblyTime.inWeeks());
  }

  @Test
  public void
      givenTwoOrdersWithSameType_whenComputeTimeToProduceOneOfModel_thenReturnTimeToProduceOneModel() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(A_MODEL_ORDER);
    AssemblyTime expectedAssemblyTime = A_MODEL_ORDER.getAssemblyTime();

    // when
    AssemblyTime assemblyTime =
        modelManufacturer.computeTimeToProduceQuantityOfModel(1, A_MODEL_ORDER.getModelType());

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedAssemblyTime.inWeeks());
  }

  @Test
  public void
      givenTwoOrdersInQueue_whenComputeTimeToProduceThreeOfModel_thenThrowInvalidModelQuantityInQueueException() {
    // given
    modelManufacturer.addOrder(A_MODEL_ORDER);
    modelManufacturer.addOrder(A_MODEL_ORDER);

    // when
    Executable computingTimeToProduceThreeOfModel =
        () ->
            modelManufacturer.computeTimeToProduceQuantityOfModel(3, A_MODEL_ORDER.getModelType());

    // then
    assertThrows(InvalidModelQuantityInQueueException.class, computingTimeToProduceThreeOfModel);
  }

  private void advanceMultipleTimes(int numberOfAdvance) {
    for (int i = 0; i < numberOfAdvance; i++) {
      modelManufacturer.advanceTime();
    }
  }
}
