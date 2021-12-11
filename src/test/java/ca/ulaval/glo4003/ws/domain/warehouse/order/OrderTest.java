package ca.ulaval.glo4003.ws.domain.warehouse.order;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTest {
  private static final OrderId AN_ID = new OrderId("id");
  private static final LocalDate A_LOCAL_DATE = LocalDate.of(23, 1, 1);
  private static final AssemblyTime AN_INITIAL_ASSEMBLY_TIME = new AssemblyTime(1);
  private static final AssemblyTime A_PRODUCTION_TIME = new AssemblyTime(10);

  @Mock private BatteryOrder batteryOrder;
  @Mock private ModelOrder modelOrder;
  @Mock private AssemblyTime assemblyTime;

  private Order order;

  @BeforeEach
  void setUp() {
    given(batteryOrder.getAssemblyTime()).willReturn(new AssemblyTime(1));
    given(modelOrder.getAssemblyTime()).willReturn(new AssemblyTime(2));

    order = new Order(AN_ID, modelOrder, batteryOrder, A_LOCAL_DATE, AN_INITIAL_ASSEMBLY_TIME);
    order.setRemainingAssemblyTime(A_PRODUCTION_TIME);
  }

  @Test
  public void whenAdvance_thenRemainingAssemblyTimeDecreasedByOne() {
    // given
    AssemblyTime expectedAssemblyTime = A_PRODUCTION_TIME.subtractWeeks(1);

    // when
    order.advance();

    // then
    assertThat(order.getRemainingAssemblyTime()).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void givenAssemblyTimeOver_whenIsOver_thenReturnTrue() {
    // given
    given(assemblyTime.isOver()).willReturn(true);
    order.setRemainingAssemblyTime(assemblyTime);

    // when
    boolean isOrderOver = order.isOver();

    // then
    assertThat(isOrderOver).isTrue();
  }

  @Test
  public void givenAssemblyTimeOver_whenIsOver_thenReturnFalse() {
    // given
    given(assemblyTime.isOver()).willReturn(false);
    order.setRemainingAssemblyTime(assemblyTime);

    // when
    boolean isOrderOver = order.isOver();

    // then
    assertThat(isOrderOver).isFalse();
  }

  @Test
  public void whenAddDelay_thenDelayIsAdded() {
    // given
    AssemblyTime aDelay = new AssemblyTime(1);

    // when
    order.addAssemblyDelay(aDelay);

    // then
    assertThat(order.getAssemblyDelay()).isEqualTo(aDelay);
  }

  @Test
  public void
      givenNoDelay_whenComputeDeliveryDate_thenReturnCreatedAtPlusSumOfModelBatteryAndAssemblyAssemblyTime() {
    // given
    LocalDate createdAt = LocalDate.of(10, 10, 10);
    Order order = new Order(AN_ID, modelOrder, batteryOrder, createdAt, AN_INITIAL_ASSEMBLY_TIME);
    LocalDate expectedDeliveryDate = LocalDate.of(10, 11, 7);

    // when
    LocalDate actualDeliveryDate = order.computeDeliveryDate();

    // then
    assertThat(actualDeliveryDate).isEquivalentAccordingToCompareTo(expectedDeliveryDate);
  }

  @Test
  public void
      givenTwoWeeksDelay_whenComputeDeliveryDate_thenReturnCreatedAtPlusInitialAssemblyTimePlusTwoWeeks() {
    // given
    LocalDate createdAt = LocalDate.of(10, 10, 10);
    Order order = new Order(AN_ID, modelOrder, batteryOrder, createdAt, AN_INITIAL_ASSEMBLY_TIME);
    AssemblyTime aDelay = new AssemblyTime(2);
    order.addAssemblyDelay(aDelay);
    LocalDate expectedDeliveryDate = LocalDate.of(10, 11, 21);

    // when
    LocalDate deliveryDate = order.computeDeliveryDate();

    // then
    assertThat(deliveryDate).isEquivalentAccordingToCompareTo(expectedDeliveryDate);
  }

  @Test
  public void givenAnOrderAndItsTransaction_whenIsRelatedToTransaction_thenReturnTrue() {
    // given
    LocalDate createdAt = LocalDate.of(10, 10, 10);
    Order order = new Order(AN_ID, modelOrder, batteryOrder, createdAt, AN_INITIAL_ASSEMBLY_TIME);
    TransactionId transactionId = new TransactionId(order.getId().toString());

    // when
    boolean isRelatedToTransaction = order.isRelatedToTransaction(transactionId);

    // then
    assertThat(isRelatedToTransaction).isTrue();
  }

  @Test
  public void givenAnOrderAndARandomTransaction_whenIsRelatedToTransaction_thenReturnFalse() {
    // given
    LocalDate createdAt = LocalDate.of(10, 10, 10);
    Order order = new Order(AN_ID, modelOrder, batteryOrder, createdAt, AN_INITIAL_ASSEMBLY_TIME);
    TransactionId transactionId =
        new TransactionId(order.getId().toString().concat("randomsuffix"));

    // when
    boolean isRelatedToTransaction = order.isRelatedToTransaction(transactionId);

    // then
    assertThat(isRelatedToTransaction).isFalse();
  }
}
