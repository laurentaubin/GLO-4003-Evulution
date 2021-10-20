package ca.ulaval.glo4003.ws.api.shared;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;

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
