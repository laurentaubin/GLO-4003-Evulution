package ca.ulaval.glo4003.ws.api.production;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.service.assembly.AssemblyLineService;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductionLineResourceImplTest {

  @Mock private Session session;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private AssemblyLineService productionLineService;
  @Mock private RoleHandler roleHandler;

  private ProductionLineResource productionLineResource;

  @BeforeEach
  void setUp() {
    productionLineResource = new ProductionLineResourceImpl(productionLineService, roleHandler);
  }

  @Test
  void givenAuthorizedRole_whenShutdown_thenShutdownCalled() {
    // given
    givenAuthorizedRole();

    // when
    productionLineResource.shutdown(containerRequestContext);

    // then
    verify(productionLineService).shutdown();
  }

  @Test
  void givenUnauthorizedRole_whenShutdown_thenExceptionThrown() {
    // given
    givenUnauthorizedRole();

    // when
    Executable action = () -> productionLineResource.shutdown(containerRequestContext);

    // then
    assertThrows(UnauthorizedUserException.class, action);
  }

  @Test
  void givenAuthorizedRole_whenActivate_thenActivateCalled() {
    // given
    givenAuthorizedRole();

    // when
    productionLineResource.reactivate(containerRequestContext);

    // then
    verify(productionLineService).activate();
  }

  @Test
  void givenUnauthorizedRole_whenReactivate_thenExceptionThrown() {
    // given
    givenUnauthorizedRole();

    // when
    Executable action = () -> productionLineResource.reactivate(containerRequestContext);

    // then
    assertThrows(UnauthorizedUserException.class, action);
  }

  private void givenUnauthorizedRole() {
    doThrow(UnauthorizedUserException.class).when(roleHandler).retrieveSession(any(), any());
  }

  private void givenAuthorizedRole() {
    given(roleHandler.retrieveSession(any(), any())).willReturn(session);
  }
}
