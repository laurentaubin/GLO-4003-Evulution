package ca.ulaval.glo4003.ws.domain.delivery;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotReadyException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;

import java.util.List;

public class DeliveryService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private final DeliveryFactory deliveryFactory;
  private final DeliveryRepository deliveryRepository;
  private final PaymentService paymentService;
  private final OrderRepository orderRepository;

  public DeliveryService() {
    this(
        serviceLocator.resolve(DeliveryFactory.class),
        serviceLocator.resolve(DeliveryRepository.class),
        serviceLocator.resolve(PaymentService.class),
        serviceLocator.resolve(OrderRepository.class));
  }

  public DeliveryService(
      DeliveryFactory deliveryFactory,
      DeliveryRepository deliveryRepository,
      PaymentService paymentService,
      OrderRepository orderRepository) {
    this.deliveryFactory = deliveryFactory;
    this.deliveryRepository = deliveryRepository;
    this.paymentService = paymentService;
    this.orderRepository = orderRepository;
  }

  public Delivery createDelivery() {
    Delivery delivery = deliveryFactory.createDelivery();
    deliveryRepository.save(delivery);
    return delivery;
  }

  public void addDeliveryDestination(
      DeliveryId deliveryId, DeliveryDestination deliveryDestination) {
    Delivery delivery = deliveryRepository.find(deliveryId);
    delivery.setDeliveryLocation(deliveryDestination);
    deliveryRepository.update(delivery);
  }

  public Receipt generateTransactionReceipt(TransactionId transactionId) {
    List<Order> ordersReadyToBeDelivered = orderRepository.findAllCompletedOrders();
    for (Order order : ordersReadyToBeDelivered) {
      if (order.isRelatedToTransaction(transactionId)) {
        Receipt receipt = paymentService.generateReceipt(transactionId);
        orderRepository.remove(order);
        return receipt;
      }
    }
    throw new DeliveryNotReadyException();
  }
}
