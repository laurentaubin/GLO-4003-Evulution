package ca.ulaval.glo4003.ws.domain.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");
  private static final DeliveryMode A_DELIVERY_MODE = DeliveryMode.CAMPUS;
  private static final Location A_LOCATION = Location.VACHON;

  @Mock private DeliveryRepository deliveryRepository;
  @Mock private DeliveryFactory deliveryFactory;
  @Mock private Delivery aDelivery;

  private DeliveryService deliveryService;
  private Delivery delivery;
  private DeliveryDestination deliveryDestination;

  @BeforeEach
  public void setUp() {
    delivery = createDeliveryWithId();
    deliveryService = new DeliveryService(deliveryFactory, deliveryRepository);
  }

  @Test
  public void givenADeliveryId_whenCreateDelivery_thenRepoSavesDelivery() {
    // given
    Delivery delivery = createDeliveryWithId();
    given(deliveryFactory.createDelivery()).willReturn(delivery);

    // when
    deliveryService.createDelivery();

    // then
    verify(deliveryRepository).save(delivery);
  }

  @Test
  public void
      givenDeliveryDestinationAndDeliveryId_whenAddDeliveryDestination_thenRepositoryUpdateDelivery() {
    // given
    deliveryDestination = givenADeliveryDestination();
    given(deliveryRepository.find(A_DELIVERY_ID)).willReturn(delivery);

    // when
    deliveryService.addDeliveryDestination(A_DELIVERY_ID, deliveryDestination);

    // then
    verify(deliveryRepository).update(delivery);
  }

  @Test
  public void whenCreateDelivery_thenReturnDelivery() {
    // given
    Delivery delivery = createDeliveryWithId();
    given(deliveryFactory.createDelivery()).willReturn(delivery);

    // when
    Delivery actualDelivery = deliveryService.createDelivery();

    // then
    assertThat(actualDelivery).isEqualTo(delivery);
  }

  @Test
  public void whenAddDeliveryDestination_thenLocationIsSetInDelivery() {
    // given
    given(deliveryRepository.find(A_DELIVERY_ID)).willReturn(aDelivery);

    // when
    deliveryService.addDeliveryDestination(A_DELIVERY_ID, deliveryDestination);

    // then
    verify(aDelivery).setDeliveryLocation(deliveryDestination);
  }

  @Test
  private DeliveryDestination givenADeliveryDestination() {
    return new DeliveryDestination(A_DELIVERY_MODE, A_LOCATION);
  }

  private Delivery createDeliveryWithId() {
    return new Delivery(A_DELIVERY_ID);
  }
}
