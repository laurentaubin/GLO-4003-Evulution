package ca.ulaval.glo4003.ws.domain.delivery;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryTest {

  @Mock private DeliveryId deliveryId;
  @Mock private DeliveryId anotherDeliveryId;
  @Mock private DeliveryDestination destination;

  @Test
  public void givenTwoEqualDeliveries_whenEquals_thenReturnTrue() {
    // given
    Delivery aDelivery = createDelivery(deliveryId, destination);
    Delivery anotherDelivery = createDelivery(deliveryId, destination);

    // when
    boolean areEqual = aDelivery.equals(anotherDelivery);

    // then
    assertThat(areEqual).isTrue();
  }

  @Test
  public void givenTwoDifferentDeliveries_whenEquals_thenReturnFalse() {
    // given
    Delivery aDelivery = createDelivery(deliveryId, destination);
    Delivery anotherDelivery = createDelivery(anotherDeliveryId, destination);

    // when
    boolean areEqual = aDelivery.equals(anotherDelivery);

    // then
    assertThat(areEqual).isFalse();
  }

  @Test
  public void whenHash_thenReturnHash() {
    // given
    Delivery aDelivery = createDelivery(deliveryId, destination);

    // when
    int hashedDelivery = aDelivery.hashCode();

    // then
    assertThat(hashedDelivery).isNotNull();
  }

  private Delivery createDelivery(DeliveryId deliveryId, DeliveryDestination deliveryDestination) {
    Delivery aDelivery = new Delivery();
    aDelivery.setDeliveryId(deliveryId);
    aDelivery.setDeliveryLocation(deliveryDestination);
    return aDelivery;
  }
}
