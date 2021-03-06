package ca.ulaval.glo4003.ws.infrastructure.warehouse;

import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

class CommandIdFactoryTest {
  private static final String AN_ID = UUID.randomUUID().toString();
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);

  @Test
  public void
      givenAnOrderId_whenGetOrCreateFromOrderId_thenCreateACommandIdWithSameIdValueAsOrderId() {
    // given
    CommandIdFactory commandIdFactory = new CommandIdFactory();

    // when
    CommandID commandId = commandIdFactory.getOrCreateFromOrderId(AN_ORDER_ID);

    // then
    assertThat(commandId.getValue()).isEqualTo(AN_ID);
  }

  @Test
  public void
      givenACommandIdAlreadyCreatedForAnOrderId_whenGetOrCreateFromOrderId_thenReturnTheSameCommandId() {
    // given
    CommandIdFactory commandIdFactory = new CommandIdFactory();
    CommandID firstCommandId = commandIdFactory.getOrCreateFromOrderId(AN_ORDER_ID);

    // when
    CommandID commandId = commandIdFactory.getOrCreateFromOrderId(AN_ORDER_ID);

    // then
    assertThat(commandId).isEqualTo(firstCommandId);
  }
}
