package ca.ulaval.glo4003.ws.infrastructure.assembly;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CommandIdFactoryTest {
  private static final String AN_ID = UUID.randomUUID().toString();
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);

  @Test
  public void givenAnOrderId_whenCreate_thenCreateACommandIdWithSameIdValueAsOrderId() {
    // given
    CommandIdFactory commandIdFactory = new CommandIdFactory();

    // when
    CommandID commandId = commandIdFactory.createFromOrderId(AN_ORDER_ID);

    // then
    assertThat(commandId.getValue()).isEqualTo(AN_ID);
  }
}
