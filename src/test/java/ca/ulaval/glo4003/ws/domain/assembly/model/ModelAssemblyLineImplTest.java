package ca.ulaval.glo4003.ws.domain.assembly.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelAssemblyLineImplTest {

  @Test
  public void whenCreate_thenModelAssemblyLineImplIsNotNull() {
    // when
    LinearModelAssemblyLine modelAssemblyLine = new LinearModelAssemblyLine();

    // then
    assertThat(modelAssemblyLine).isNotNull();
  }
}
