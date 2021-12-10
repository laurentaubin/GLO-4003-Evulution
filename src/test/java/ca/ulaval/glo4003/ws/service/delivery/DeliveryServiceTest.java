package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.delivery.*;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {
  private static final DeliveryId DELIVERY_ID = new DeliveryId("ID");
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("tx id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");
  private static final DeliveryMode A_DELIVERY_MODE = DeliveryMode.CAMPUS;
  private static final Location A_LOCATION = Location.VACHON;

  @Mock private CompletedDeliveryDtoAssembler deliveryResponseAssembler;
  @Mock private DeliveryDestinationAssembler deliveryDestinationAssembler;
  @Mock private DeliveryLocationDto deliveryLocationRequest;
  @Mock private DeliveryDestination deliveryDestination;
  @Mock private DeliveryRepository deliveryRepository;
  @Mock private PaymentService paymentService;
  @Mock private DeliveryFactory deliveryFactory;
  @Mock private OrderRepository orderRepository;
  @Mock private Delivery delivery;
  @Mock private Order order;
  @Mock private DeliveryLocationDto deliveryLocationDto;

  private DeliveryService deliveryService;

  @BeforeEach
  void setUp() {
    deliveryService =
        new DeliveryService(
            deliveryResponseAssembler,
            deliveryDestinationAssembler,
            deliveryFactory,
            deliveryRepository,
            paymentService,
            orderRepository);
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
    DeliveryDestination deliveryDestination = givenADeliveryDestination();

    given(deliveryDestinationAssembler.assemble(any())).willReturn(deliveryDestination);
    given(deliveryRepository.find(DELIVERY_ID)).willReturn(delivery);

    // when
    deliveryService.addDeliveryLocation(DELIVERY_ID, deliveryLocationDto);

    // then
    verify(deliveryRepository).update(delivery);
  }

  @Test
  public void whenAddDeliveryDestination_thenLocationIsSetInDelivery() {
    // given
    given(deliveryRepository.find(A_DELIVERY_ID)).willReturn(delivery);
    given(deliveryDestinationAssembler.assemble(deliveryLocationRequest))
        .willReturn(deliveryDestination);

    // when
    deliveryService.addDeliveryLocation(A_DELIVERY_ID, deliveryLocationRequest);

    // then
    verify(delivery).setDeliveryLocation(deliveryDestination);
  }

  @Test
  public void givenATransactionId_whenCompleteDelivery_thenPaymentServiceGeneratesReceipt() {
    // given
    given(order.isRelatedToTransaction(A_TRANSACTION_ID)).willReturn(true);
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(order));

    // when
    deliveryService.completeDelivery(A_TRANSACTION_ID);

    // then
    verify(paymentService).generateReceipt(A_TRANSACTION_ID);
  }

  private DeliveryDestination givenADeliveryDestination() {
    return new DeliveryDestination(A_DELIVERY_MODE, A_LOCATION);
  }

  private Delivery createDeliveryWithId() {
    return new Delivery(A_DELIVERY_ID);
  }
}
