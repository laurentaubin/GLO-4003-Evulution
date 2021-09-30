package ca.ulaval.glo4003.ws.api.util;

import static com.google.common.truth.Truth.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LocalDateProviderTest {

  @Test
  public void whenToday_thenReturnLocalDate() {
    // given
    LocalDateProvider localDateProvider = new LocalDateProvider();

    // when
    LocalDate today = localDateProvider.today();

    // then
    assertThat(today).isNotNull();
  }
}
