package ca.ulaval.glo4003.ws.domain.manufacturer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManufacturerSchedulerTest {
  private static final Times ONE_TIME = new Times(1);

  @Mock PeriodicManufacturer modelManufacturer;
  @Mock PeriodicManufacturer batteryManufacturer;
  @Mock PeriodicManufacturer vehicleManufacturer;
  @Mock ShutdownObserver shutdownObserver;
  @Mock ReactivateObserver reactivateObserver;

  private ManufacturerScheduler manufacturerScheduler;

  @BeforeEach
  public void setUp() {
    manufacturerScheduler =
        new ManufacturerScheduler(modelManufacturer, batteryManufacturer, vehicleManufacturer);
    manufacturerScheduler.registerReactivateObserver(reactivateObserver);
    manufacturerScheduler.registerShutdownObserver(shutdownObserver);
  }

  @Test
  public void whenAdvanceTime_thenAdvanceTimeOnAllPeriodicManufacturerInOrder() {
    // when
    manufacturerScheduler.advanceTime();

    // then
    InOrder inOrder = Mockito.inOrder(modelManufacturer, batteryManufacturer, vehicleManufacturer);
    inOrder.verify(vehicleManufacturer).advanceTime();
    inOrder.verify(batteryManufacturer).advanceTime();
    inOrder.verify(modelManufacturer).advanceTime();
  }

  @Test
  public void whenShutdown_thenStopAllManufacturer() {
    // when
    manufacturerScheduler.shutdown();

    // then
    verify(modelManufacturer).stop();
    verify(batteryManufacturer).stop();
    verify(vehicleManufacturer).stop();
  }

  @Test
  public void whenShutdownMultipleTimesInARow_thenNotifyShutdownOnlyOnce() {
    // when
    manufacturerScheduler.shutdown();
    manufacturerScheduler.shutdown();
    manufacturerScheduler.shutdown();

    // then
    verify(shutdownObserver, ONE_TIME).listenToAssemblyShutdown();
  }

  @Test
  public void whenShutdown_thenNotifyShutdown() {
    // when
    manufacturerScheduler.shutdown();

    // then
    verify(shutdownObserver).listenToAssemblyShutdown();
  }

  @Test
  public void givenAShutdown_whenAdvanceTime_thenDoNotAdvanceManufacturers() {
    // given
    manufacturerScheduler.shutdown();

    // when
    manufacturerScheduler.advanceTime();

    // then
    verify(modelManufacturer, never()).advanceTime();
    verify(batteryManufacturer, never()).advanceTime();
    verify(vehicleManufacturer, never()).advanceTime();
  }

  @Test
  public void givenShutdown_whenReactivate_thenNotifyReactivation() {
    // given
    manufacturerScheduler.shutdown();

    // when
    manufacturerScheduler.reactive();

    // then
    verify(reactivateObserver).listenToAssemblyReactivation();
  }

  @Test
  public void givenShutdown_whenReactivateMultipleTimesInARow_thenNotifyReactivationOnlyOnce() {
    // given
    manufacturerScheduler.shutdown();

    // when
    manufacturerScheduler.reactive();
    manufacturerScheduler.reactive();
    manufacturerScheduler.reactive();

    // then
    verify(reactivateObserver, ONE_TIME).listenToAssemblyReactivation();
  }

  @Test
  public void givenAlreadyActivated_whenReactivate_thenAssemblyReactivationEventNotEmitted() {
    // when
    manufacturerScheduler.reactive();

    // then
    verify(reactivateObserver, never()).listenToAssemblyReactivation();
  }
}
