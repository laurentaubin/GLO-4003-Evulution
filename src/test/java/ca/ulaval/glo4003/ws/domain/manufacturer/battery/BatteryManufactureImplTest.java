package ca.ulaval.glo4003.ws.domain.manufacturer.battery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.BatteryOrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BatteryManufactureImplTest {
  private static final int THREE_WEEKS = 3;
  private static final int FIVE_WEEKS = 5;
  private static final OrderId AN_ID = new OrderId("id");
  private static final AssemblyTime AN_ASSEMBLY_TIME = new AssemblyTime(5);
  private static final String A_BATTERY_TYPE = "batteryType";
  private static final BatteryOrder A_BATTERY_ORDER =
      new BatteryOrderBuilder()
          .withOrderId(AN_ID)
          .withAssemblyTime(AN_ASSEMBLY_TIME)
          .withBatteryType(A_BATTERY_TYPE)
          .build();
  private static final OrderId ANOTHER_ID = new OrderId("id");
  private static final AssemblyTime ANOTHER_ASSEMBLY_TIME = new AssemblyTime(10);
  private static final String ANOTHER_BATTERY_TYPE = "anotherBatteryType";
  private static final BatteryOrder ANOTHER_BATTERY_ORDER =
      new BatteryOrderBuilder()
          .withBatteryType(ANOTHER_BATTERY_TYPE)
          .withOrderId(ANOTHER_ID)
          .withAssemblyTime(ANOTHER_ASSEMBLY_TIME)
          .build();

  @Mock private BatteryAssemblyLineAdapter batteryAssemblyLineAdapter;
  @Mock private BatteryAssembledObserver batteryAssembledObserver;

  private BatteryManufacturerImpl batteryManufacturer;

  @BeforeEach
  public void setUp() {
    batteryManufacturer = new BatteryManufacturerImpl(batteryAssemblyLineAdapter);
    batteryManufacturer.register(batteryAssembledObserver);
  }

  @Test
  public void givenABatteryOrderAssembled_whenAdvance_thenNotifyObserversBatteryOrderIsAssembled() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    batteryManufacturer.advanceTime();

    // then
    verify(batteryAssembledObserver).listenToBatteryAssembled(A_BATTERY_ORDER);
  }

  @Test
  public void givenBatteryOrderInProgress_whenAdvance_thenObserversAreNotNotifyOfAnything() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);

    // when
    batteryManufacturer.advanceTime();

    // then
    verify(batteryAssembledObserver, never()).listenToBatteryAssembled(A_BATTERY_ORDER);
  }

  @Test
  public void givenManyBatteryOrdersMade_whenAdvance_thenOnlyTheFirstOneIsSentToBeAssembled() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    batteryManufacturer.addOrder(ANOTHER_BATTERY_ORDER);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    batteryManufacturer.advanceTime();

    // then
    verify(batteryAssembledObserver).listenToBatteryAssembled(A_BATTERY_ORDER);
    verify(batteryAssembledObserver, never()).listenToBatteryAssembled(ANOTHER_BATTERY_ORDER);
  }

  @Test
  public void
      givenManyBatteryOrdersMadeAndFirstOneIsAssembled_whenAdvance_thenSecondOrderIsSentToBeAssembled() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    batteryManufacturer.addOrder(ANOTHER_BATTERY_ORDER);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    batteryManufacturer.advanceTime();

    // then
    verify(batteryAssemblyLineAdapter).addOrder(ANOTHER_BATTERY_ORDER);
  }

  @Test
  public void
      givenABatteryOrderAssembled_whenAdvanceManyTimes_thenNotifyOnlyOnceAndDoNothingAfter() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ID)).willReturn(AssemblyStatus.ASSEMBLED);

    // when
    advanceTimeManyTimes(FIVE_WEEKS);

    // then
    assertThat(mockingDetails(batteryAssembledObserver).getInvocations().size()).isEqualTo(1);
  }

  @Test
  public void
      givenADesiredBatteryTypeJustReceivedForAssembly_whenComputeRemainingTimeToProduceNextBatteryType_thenReturnRemainingAssemblyTimeOfCurrentBatteryOrderBeingAssembled() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);

    // when
    AssemblyTime assemblyTime =
        batteryManufacturer.computeRemainingTimeToProduceNextBatteryType(
            A_BATTERY_ORDER.getBatteryType());

    // then
    assertThat(assemblyTime).isEqualTo(A_BATTERY_ORDER.getAssemblyTime());
  }

  @Test
  public void
      givenADesiredBatteryTypeIsInProgressAfterSomeTime_whenComputeRemainingTimeToProduceNextBatteryType_thenReturnRemainingAssemblyTimeOfCurrentBatteryOrderBeingAssembled() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    advanceTimeManyTimes(THREE_WEEKS);
    AssemblyTime expectedAssemblyTime = AN_ASSEMBLY_TIME.subtract(new AssemblyTime(THREE_WEEKS));

    // when
    AssemblyTime assemblyTime =
        batteryManufacturer.computeRemainingTimeToProduceNextBatteryType(
            A_BATTERY_ORDER.getBatteryType());

    // then
    assertThat(assemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenOrdersForDifferentTypesAndAskingForRemainingTimeOfSecondBatteryType_whenComputeRemainingTimeToProduceNextBatteryType_thenReturnRemainingAssemblyTimeConsideringPositionInQueue() {
    // given
    batteryManufacturer.addOrder(A_BATTERY_ORDER);
    batteryManufacturer.addOrder(ANOTHER_BATTERY_ORDER);
    AssemblyTime expectedAssemblyTime =
        ANOTHER_BATTERY_ORDER.getAssemblyTime().add(A_BATTERY_ORDER.getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        batteryManufacturer.computeRemainingTimeToProduceNextBatteryType(
            ANOTHER_BATTERY_ORDER.getBatteryType());

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedAssemblyTime.inWeeks());
  }

  private void advanceTimeManyTimes(int numberOfAdvance) {
    for (int i = 0; i < numberOfAdvance; i++) {
      batteryManufacturer.advanceTime();
    }
  }
}
