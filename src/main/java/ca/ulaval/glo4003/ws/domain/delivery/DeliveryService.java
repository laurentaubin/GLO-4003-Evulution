package ca.ulaval.glo4003.ws.domain.delivery;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;

public class DeliveryService {
  private final DeliveryFactory deliveryFactory;
  private final DeliveryRepository deliveryRepository;
  private final PaymentService paymentService;

  public DeliveryService(
      DeliveryFactory deliveryFactory,
      DeliveryRepository deliveryRepository,
      PaymentService paymentService) {
    this.deliveryFactory = deliveryFactory;
    this.deliveryRepository = deliveryRepository;
    this.paymentService = paymentService;
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
    return paymentService.generateReceipt(transactionId);
  }
}
