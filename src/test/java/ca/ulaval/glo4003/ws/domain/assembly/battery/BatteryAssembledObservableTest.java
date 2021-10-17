package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BatteryAssembledObservableTest {

  @Mock private BatteryAssembledObserver batteryAssembledObserver;
  @Mock private Order order;

  @Test
  public void givenRegisteredObserver_whenNotifyBatteryCompleted_thenNotifyAllObservers() {
    // given
    BatteryAssembledObservable batteryAssembledObservable = new BatteryAssembledObservableImpl();
    batteryAssembledObservable.register(batteryAssembledObserver);

    // when
    batteryAssembledObservable.notifyBatteryCompleted(order);

    // then
    verify(batteryAssembledObserver).listenToBatteryAssembled(order);
  }
}

class BatteryAssembledObservableImpl extends BatteryAssembledObservable {
  public BatteryAssembledObservableImpl() {
    super();
  }

  @Override
  public void register(BatteryAssembledObserver observer) {
    super.register(observer);
  }

  @Override
  public void notifyBatteryCompleted(Order order) {
    super.notifyBatteryCompleted(order);
  }
}
