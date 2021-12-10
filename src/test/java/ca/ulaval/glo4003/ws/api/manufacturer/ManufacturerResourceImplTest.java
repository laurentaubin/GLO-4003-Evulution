package ca.ulaval.glo4003.ws.api.manufacturer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManufacturerResourceImplTest {

  @Mock private Session session;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private ManufacturerService manufacturerService;
  @Mock private RoleHandler roleHandler;

  private ManufacturerResource manufacturerResource;

  @BeforeEach
  void setUp() {
    manufacturerResource = new ManufacturerResourceImpl(manufacturerService, roleHandler);
  }

  @Test
  void givenAuthorizedRole_whenShutdown_thenShutdownCalled() {
    // given
    givenAuthorizedRole();

    // when
    manufacturerResource.shutdown(containerRequestContext);

    // then
    verify(manufacturerService).shutdown();
  }

  @Test
  void givenUnauthorizedRole_whenShutdown_thenExceptionThrown() {
    // given
    givenUnauthorizedRole();

    // when
    Executable triggeringShutdown = () -> manufacturerResource.shutdown(containerRequestContext);

    // then
    assertThrows(UnauthorizedUserException.class, triggeringShutdown);
  }

  @Test
  void givenAuthorizedRole_whenActivate_thenActivateCalled() {
    // given
    givenAuthorizedRole();

    // when
    manufacturerResource.reactivate(containerRequestContext);

    // then
    verify(manufacturerService).reactivate();
  }

  @Test
  void givenUnauthorizedRole_whenReactivate_thenExceptionThrown() {
    // given
    givenUnauthorizedRole();

    // when
    Executable reactivating = () -> manufacturerResource.reactivate(containerRequestContext);

    // then
    assertThrows(UnauthorizedUserException.class, reactivating);
  }

  private void givenUnauthorizedRole() {
    doThrow(UnauthorizedUserException.class).when(roleHandler).retrieveSession(any(), any());
  }

  private void givenAuthorizedRole() {
    given(roleHandler.retrieveSession(any(), any())).willReturn(session);
  }
}
