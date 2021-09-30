package ca.ulaval.glo4003.ws.infrastructure.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.delivery.*;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class InMemoryDeliveryRepositoryTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");
  private static final DeliveryDestination A_DELIVERY_LOCATION =
      new DeliveryDestination(DeliveryMode.CAMPUS, Location.VACHON);

  private DeliveryRepository deliveryRepository;

  @BeforeEach
  void setUp() {
    deliveryRepository = new InMemoryDeliveryRepository();
  }

  @Test
  void givenSavedDeliveryInRepository_whenFindDelivery_thenReturnDelivery() {
    // given
    Delivery savedDelivery = createValidDelivery();
    deliveryRepository.save(savedDelivery);

    // when
    Delivery foundDelivery = deliveryRepository.find(savedDelivery.getDeliveryId());

    // then
    assertThat(foundDelivery).isEqualTo(savedDelivery);
  }

  @Test
  void givenInvalidDeliveryId_whenFindDelivery_thenThrowDeliveryNotFoundException() {
    // given
    DeliveryId invalidId = new DeliveryId("invalid id");

    // when
    Executable exception = () -> deliveryRepository.find(invalidId);

    // then
    assertThrows(DeliveryNotFoundException.class, exception);
  }

  @Test
  void givenDeliveryAlreadyInRepository_whenSave_thenThrowDuplicateDeliveryException() {
    // given
    Delivery delivery = createValidDelivery();
    deliveryRepository.save(delivery);

    // when
    Executable exception = () -> deliveryRepository.save(delivery);

    // given
    assertThrows(DuplicateDeliveryException.class, exception);
  }

  @Test
  void givenDeliveryInRepository_whenUpdate_thenUpdateDelivery() {
    // given
    Delivery originalDelivery = new Delivery(AN_ID);
    deliveryRepository.save(originalDelivery);
    Delivery updatedDelivery = createValidDelivery();

    // when
    deliveryRepository.update(updatedDelivery);

    // then
    Delivery foundDelivery = deliveryRepository.find(AN_ID);
    assertThat(foundDelivery).isEqualTo(updatedDelivery);
    assertThat(foundDelivery).isNotEqualTo(originalDelivery);
  }

  @Test
  void givenDeliveryNotInRepository_whenUpdate_thenThrowDeliveryNotFound() {
    // given
    Delivery delivery = createValidDelivery();

    // when
    Executable exception = () -> deliveryRepository.update(delivery);

    // then
    assertThrows(DeliveryNotFoundException.class, exception);
  }

  private Delivery createValidDelivery() {
    Delivery delivery = new Delivery();
    delivery.setDeliveryId(AN_ID);
    delivery.setDeliveryLocation(A_DELIVERY_LOCATION);
    return delivery;
  }
}
