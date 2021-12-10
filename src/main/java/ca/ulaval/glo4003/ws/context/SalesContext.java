package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.context.exception.CouldNotLoadPropertiesFileException;
import ca.ulaval.glo4003.ws.domain.report.ReportsService;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportFactory;
import ca.ulaval.glo4003.ws.domain.report.sales.SalesReportIssuer;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionCompletedObservable;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionFactory;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogFactory;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogFinder;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogService;
import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogSink;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailContent;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailServer;
import ca.ulaval.glo4003.ws.infrastructure.communication.report.sales.EmailSalesReportIssuer;
import ca.ulaval.glo4003.ws.infrastructure.communication.report.sales.ReportType;
import ca.ulaval.glo4003.ws.infrastructure.communication.report.sales.SalesReportEmailFactory;
import ca.ulaval.glo4003.ws.infrastructure.transaction.log.InMemoryTransactionLogRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.log.TransactionLogDtoAssembler;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.warehouse.WarehouseService;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SalesContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerTransactionLogs();
    registerSalesReport();
    registerServices();
  }

  private void registerTransactionLogs() {
    InMemoryTransactionLogRepository transactionLogRepository =
        new InMemoryTransactionLogRepository(new TransactionLogDtoAssembler());
    serviceLocator.register(TransactionLogSink.class, transactionLogRepository);
    serviceLocator.register(TransactionLogFinder.class, transactionLogRepository);

    TransactionLogFactory transactionLogFactory =
        new TransactionLogFactory(serviceLocator.resolve(LocalDateProvider.class));
    TransactionLogService transactionLogService =
        new TransactionLogService(
            transactionLogFactory, serviceLocator.resolve(TransactionLogSink.class));
    serviceLocator.register(TransactionLogService.class, transactionLogService);
  }

  private void registerSalesReport() {
    Properties emailConfig = openEmailConfig();

    SalesReportFactory salesReportFactory =
        new SalesReportFactory(serviceLocator.resolve(TransactionLogFinder.class));

    Map<ReportType, EmailContent> salesReportTemplates = createSalesReportTemplate();
    SalesReportEmailFactory salesReportEmailFactory =
        new SalesReportEmailFactory(
            serviceLocator.resolve(EmailServer.class), salesReportTemplates);
    SalesReportIssuer salesReportIssuer =
        new EmailSalesReportIssuer(
            emailConfig.getProperty("email.address"), salesReportEmailFactory);
    ReportsService reportsService =
        new ReportsService(
            serviceLocator.resolve(LocalDateProvider.class),
            salesReportFactory,
            serviceLocator.resolve(UserFinder.class),
            salesReportIssuer);

    serviceLocator.register(ReportsService.class, reportsService);
  }

  private void registerServices() {
    TransactionCompletedObservable txCompletedObservable = new TransactionCompletedObservable();
    serviceLocator.register(TransactionCompletedObservable.class, txCompletedObservable);
    txCompletedObservable.register(serviceLocator.resolve(TransactionLogService.class));
    txCompletedObservable.register(serviceLocator.resolve(WarehouseService.class));

    serviceLocator.register(TransactionFactory.class, new TransactionFactory());
    serviceLocator.register(VehicleFactory.class, new VehicleFactory());
    serviceLocator.register(TransactionService.class, new TransactionService());
  }

  private Properties openEmailConfig() {
    try {
      InputStream configFile = new FileInputStream("./target/classes/emailConfig.properties");

      Properties properties = new Properties();
      properties.load(configFile);
      return properties;

    } catch (IOException e) {
      e.printStackTrace();
      throw new CouldNotLoadPropertiesFileException(e);
    }
  }

  private Map<ReportType, EmailContent> createSalesReportTemplate() {
    Map<ReportType, EmailContent> reportTemplates = new HashMap<>();
    reportTemplates.put(
        ReportType.REGULAR,
        new EmailContent(
            "Sales report for the week of %s",
            "Hello %s, \r\n\r\n Please find your weekly sales report below. \r\n\r\n Total number of units sold: %s \r\n Average price of units sold: %s \r\n Most popular vehicle model: %s \r\n Most popular battery type: %s \r\n"));
    reportTemplates.put(
        ReportType.EMPTY,
        new EmailContent(
            "Sales report for the week of %s",
            "Hello %s, \r\n\r\n There was no sales recorded this week, so the report could not be generated."));
    return reportTemplates;
  }
}
