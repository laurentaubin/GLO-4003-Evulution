package ca.ulaval.glo4003.ws.domain.shared;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalDateProviderTest {
  private static final LocalDate A_DATE = LocalDate.of(3, 3, 3);
  private static final LocalDate A_DATE_A_WEEK_LATER = LocalDate.of(3, 3, 10);

  @Mock LocalDateWrapper localDateWrapper;

  private LocalDateProvider localDateProvider;

  @BeforeEach
  public void setUp() {
    localDateProvider = new LocalDateProvider(localDateWrapper);
  }

  @Test
  public void givenInitialState_whenToday_thenReturnLocalDate() {
    // given
    given(localDateWrapper.today()).willReturn(A_DATE);

    // when
    LocalDate date = localDateProvider.today();

    // then
    assertThat(date).isEquivalentAccordingToCompareTo(A_DATE);
  }

  @Test
  public void givenSimulatedTimeIsAdvanced_whenToday_thenReturnLocalDatePlusAdvancedTime() {
    // given
    localDateProvider.advance();
    given(localDateWrapper.today()).willReturn(A_DATE);

    // when
    LocalDate date = localDateProvider.today();

    // then
    assertThat(date).isEquivalentAccordingToCompareTo(A_DATE_A_WEEK_LATER);
  }
}
