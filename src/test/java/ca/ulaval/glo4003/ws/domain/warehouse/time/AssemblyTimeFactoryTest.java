package ca.ulaval.glo4003.ws.domain.warehouse.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class AssemblyTimeFactoryTest {
  private static final int WEEKS = 42;

  private AssemblyTimeFactory assemblyTimeFactory;

  @BeforeEach
  public void setUp() {
    assemblyTimeFactory = new AssemblyTimeFactory();
  }

  @Test
  public void givenWeeks_whenCreate_thenAssemblyTimeIsCreatedWithGivenNumberOfWeeks() {
    // when
    AssemblyTime assemblyTime = assemblyTimeFactory.create(WEEKS);

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(WEEKS);
  }
}
