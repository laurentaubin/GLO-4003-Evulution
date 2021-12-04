package ca.ulaval.glo4003.ws.domain.production;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.ProductionShutdownObserver;
import ca.ulaval.glo4003.ws.service.AssemblyLineService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductionLineServiceTest {

  @Mock AssemblyLineService assemblyLine;
  @Mock ProductionShutdownObserver observer;
  @Mock List<Order> orders;

  private ProductionLineService productionLineService;

  @BeforeEach
  void setUp() {
    productionLineService = new ProductionLineService(assemblyLine);
    productionLineService.register(observer);
  }

  @Test
  public void whenReactivate_thenActivateInAssemblyLineCalled() {
    // when
    productionLineService.reactivate();

    // then
    verify(assemblyLine).activate();
  }

  @Test
  public void givenActiveOrders_whenShutdown_thenNotifyObserversWithOrders() {
    // given
    when(assemblyLine.getActiveOrders()).thenReturn(orders);

    // when
    productionLineService.shutdown();

    // then
    verify(observer).listenProductionLineShutdown(orders);
  }

  @Test
  public void whenShutdown_thenShutdownInAssemblyLineCalled() {
    // when
    productionLineService.shutdown();

    // then
    verify(assemblyLine).shutdown();
  }
}
