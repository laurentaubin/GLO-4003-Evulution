package ca.ulaval.glo4003.ws.service.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryFactory;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryMode;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.Location;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {
  private static final TransactionId TRANSACTION_ID = TransactionId.fromString("ID");
  private static final DeliveryId DELIVERY_ID = new DeliveryId("ID");
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;
  private static final Price A_PRICE = new Price(new BigDecimal(1200));
  private static final Receipt A_RECEIPT =
      new Receipt(A_PRICE, Frequency.MONTHLY, AMOUNT_OF_YEARS_TO_PAY_OVER);
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
