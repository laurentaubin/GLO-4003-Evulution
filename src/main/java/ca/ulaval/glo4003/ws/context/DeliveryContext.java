package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryFactory;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.ReceiptFactory;
import ca.ulaval.glo4003.ws.infrastructure.delivery.InMemoryDeliveryRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.InMemoryTransactionRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.TransactionAssembler;

public class DeliveryContext implements Context {
  public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;

  @Override
  public void registerContext() {
    registerRepositories();
    registerServices();
  }

  private void registerRepositories() {
    serviceLocator.register(
        TransactionRepository.class, new InMemoryTransactionRepository(new TransactionAssembler()));
    serviceLocator.register(DeliveryRepository.class, new InMemoryDeliveryRepository());
  }

  private void registerServices() {
    serviceLocator.register(DeliveryFactory.class, new DeliveryFactory());
    serviceLocator.register(ReceiptFactory.class, new ReceiptFactory(AMOUNT_OF_YEARS_TO_PAY_OVER));
    serviceLocator.register(PaymentService.class, new PaymentService());
    serviceLocator.register(DeliveryService.class, new DeliveryService());
  }
}
