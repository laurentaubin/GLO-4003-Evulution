package ca.ulaval.glo4003.ws.api.manufacturer;

import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManufacturerResourceImplTest {

  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private ManufacturerService manufacturerService;
  @Mock private TokenExtractor tokenExtractor;
  @Mock private TokenDto tokenDto;

  private ManufacturerResource manufacturerResource;

  @BeforeEach
  void setUp() {
    manufacturerResource = new ManufacturerResourceImpl(manufacturerService, tokenExtractor);
  }

  @Test
  void whenShutdown_thenShutdownCalled() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    manufacturerResource.shutdown(containerRequestContext);

    // then
    verify(manufacturerService).shutdown(tokenDto);
  }

  @Test
  void whenActivate_thenActivateCalled() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    manufacturerResource.reactivate(containerRequestContext);

    // then
    verify(manufacturerService).reactivate(tokenDto);
  }
}
