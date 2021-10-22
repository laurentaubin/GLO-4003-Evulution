package ca.ulaval.glo4003.ws.domain.assembly;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
