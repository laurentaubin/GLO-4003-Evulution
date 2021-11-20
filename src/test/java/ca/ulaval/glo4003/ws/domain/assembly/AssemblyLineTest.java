package ca.ulaval.glo4003.ws.domain.assembly;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AssemblyLineTest {

  @Mock private OrderFactory orderFactory;
  @Mock private Order anOrder;
  @Mock private AssemblyStrategy assemblyStrategy;
  @Mock private Transaction transaction;

  private AssemblyLine assemblyLine;

  @BeforeEach
  public void setUp() {
    assemblyLine = new AssemblyLine(orderFactory, assemblyStrategy);
  }

  @Test
  public void givenACompletedTransaction_whenListenToTransactionCompleted_thenOrderIsSent() {
    // given
    given(orderFactory.create(transaction)).willReturn(anOrder);

    // when
    assemblyLine.listenToTransactionCompleted(transaction);

    // then
    verify(assemblyStrategy).addOrder(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnAssemblyStrategy() {
    // when
    assemblyLine.advance();

    // then
    verify(assemblyStrategy).advance();
  }

  @Test
  public void givenShutdownProductionLine_whenAdvance_thenShouldNotCallAdvanceOnAssemblyStrategy() {
    // given
    assemblyLine.shutdown();

    // when
    assemblyLine.advance();

    // then
    verify(assemblyStrategy, never()).advance();
  }

  @Test
  public void givenActivatedProductionLineAfterShutdown_whenAdvance_thenShouldCallAdvanceOnAssemblyStrategy() {
    // given
    assemblyLine.shutdown();
    assemblyLine.activate();

    // when
    assemblyLine.advance();

    // then
    verify(assemblyStrategy).advance();
  }

  @Test
  public void givenActiveOrdersInAssembly_whenGetActiveOrders_thenShouldReturnActiveOrders() {
    // given
    var expectedOrders = new ArrayList<>(List.of(anOrder));
    given(assemblyStrategy.getActiveOrders()).willReturn(expectedOrders);

    // when
    var result = assemblyLine.getActiveOrders();

    // then
    assertThat(result).isEqualTo(expectedOrders);
  }
}
