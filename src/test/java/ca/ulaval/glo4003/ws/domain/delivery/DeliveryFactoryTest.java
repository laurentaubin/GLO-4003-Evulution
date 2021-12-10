package ca.ulaval.glo4003.ws.domain.delivery;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryFactoryTest {
  private DeliveryFactory deliveryFactory;

  @BeforeEach
  void setUp() {
    deliveryFactory = new DeliveryFactory();
  }

  @Test
  void whenCreateDelivery_thenCreatedDeliveryShouldHaveDeliveryId() {
    // when
    Delivery delivery = deliveryFactory.createDelivery();

    // then
    assertThat(delivery.getDeliveryId()).isNotNull();
  }
}
