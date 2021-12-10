package ca.ulaval.glo4003.ws.domain.shared;

import static com.google.common.truth.Truth.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LocalDateWrapperTest {
  @Test
  public void whenToday_thenReturnLocalDate() {
    // given
    LocalDateWrapper localDateWrapper = new LocalDateWrapper();

    // when
    LocalDate today = localDateWrapper.today();

    // then
    assertThat(today).isNotNull();
  }
}
