package ca.ulaval.glo4003.ws.service.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.*;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderRepository;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
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
  private static final List<Role> PRIVILEGED_ROLES = List.of(Role.CUSTOMER);

  @Mock private CompletedDeliveryDtoAssembler deliveryResponseAssembler;
  @Mock private DeliveryDestinationAssembler deliveryDestinationAssembler;
  @Mock private DeliveryLocationDto deliveryLocationRequest;
  @Mock private DeliveryDestination deliveryDestination;
  @Mock private DeliveryRepository deliveryRepository;
  @Mock private PaymentService paymentService;
  @Mock private DeliveryFactory deliveryFactory;
  @Mock private OrderRepository orderRepository;
  @Mock private TokenDto tokenDto;
  @Mock private UserService userService;

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
            orderRepository,
                userService);
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
    deliveryService.addDeliveryLocation(tokenDto, DELIVERY_ID, deliveryLocationDto);

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
    deliveryService.addDeliveryLocation(tokenDto, A_DELIVERY_ID, deliveryLocationRequest);

    // then
    verify(delivery).setDeliveryLocation(deliveryDestination);
  }

  @Test
  public void whenAddDeliveryDestination_thenVerifyIfUserIsAllowed() {
    // given
    given(deliveryRepository.find(A_DELIVERY_ID)).willReturn(delivery);
    given(deliveryDestinationAssembler.assemble(deliveryLocationRequest))
            .willReturn(deliveryDestination);

    // when
    deliveryService.addDeliveryLocation(tokenDto, A_DELIVERY_ID, deliveryLocationRequest);

    // then
    verify(userService).isAllowed(tokenDto, PRIVILEGED_ROLES);
  }

  @Test
  public void whenAddDeliveryDestination_thenValidateDeliveryOwnership() {
    // given
    given(deliveryRepository.find(A_DELIVERY_ID)).willReturn(delivery);
    given(deliveryDestinationAssembler.assemble(deliveryLocationRequest))
            .willReturn(deliveryDestination);

    // when
    deliveryService.addDeliveryLocation(tokenDto, A_DELIVERY_ID, deliveryLocationRequest);

    // then
    verify(userService).validateDeliveryOwnership(tokenDto, A_DELIVERY_ID, PRIVILEGED_ROLES);
  }

  @Test
  public void givenATransactionId_whenCompleteDelivery_thenPaymentServiceGeneratesReceipt() {
    // given
    given(order.isRelatedToTransaction(A_TRANSACTION_ID)).willReturn(true);
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(order));
    given(userService.getTransactionIdFromDeliveryId(tokenDto, A_DELIVERY_ID)).willReturn(A_TRANSACTION_ID);

    // when
    deliveryService.completeDelivery(tokenDto, A_DELIVERY_ID);

    // then
    verify(paymentService).generateReceipt(A_TRANSACTION_ID);
  }

  @Test
  public void whenCompleteDelivery_thenVerifyIfUserIsAllowed() {
    // given
    given(order.isRelatedToTransaction(A_TRANSACTION_ID)).willReturn(true);
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(order));
    given(userService.getTransactionIdFromDeliveryId(tokenDto, A_DELIVERY_ID)).willReturn(A_TRANSACTION_ID);

    // when
    deliveryService.completeDelivery(tokenDto, A_DELIVERY_ID);

    // then
    verify(userService).isAllowed(tokenDto, PRIVILEGED_ROLES);
  }

  @Test
  public void whenCompleteDelivery_thenValidateDeliveryOwnership() {
    // given
    given(order.isRelatedToTransaction(A_TRANSACTION_ID)).willReturn(true);
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(order));
    given(userService.getTransactionIdFromDeliveryId(tokenDto, A_DELIVERY_ID)).willReturn(A_TRANSACTION_ID);

    // when
    deliveryService.completeDelivery(tokenDto, A_DELIVERY_ID);

    // then
    verify(userService).validateDeliveryOwnership(tokenDto, A_DELIVERY_ID, PRIVILEGED_ROLES);
  }

  private DeliveryDestination givenADeliveryDestination() {
    return new DeliveryDestination(A_DELIVERY_MODE, A_LOCATION);
  }

  private Delivery createDeliveryWithId() {
    return new Delivery(A_DELIVERY_ID);
  }
}
