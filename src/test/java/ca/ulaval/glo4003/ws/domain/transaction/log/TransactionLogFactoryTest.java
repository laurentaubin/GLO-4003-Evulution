package ca.ulaval.glo4003.ws.domain.transaction.log;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.fixture.TransactionFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionLogFactoryTest {
  private static final LocalDate A_PRESENT_DATE = LocalDate.of(1, 1, 1);
  private final TransactionFixture transactionFixture = new TransactionFixture();

  @Mock LocalDateProvider localDateProvider;

  private TransactionLogFactory transactionLogFactory;

  @BeforeEach
  public void setUp() {
    transactionLogFactory = new TransactionLogFactory(localDateProvider);
  }

  @Test
  public void givenTransaction_whenCreate_thenTimestampIsCurrentDate() {
    // given
    given(localDateProvider.today()).willReturn(A_PRESENT_DATE);
    Transaction transaction = transactionFixture.givenACompleteTransaction();

    // when
    TransactionLogEntry transactionLogEntry = transactionLogFactory.create(transaction);

    // then
    assertThat(transactionLogEntry.getCreationDate()).isEquivalentAccordingToCompareTo(A_PRESENT_DATE);
  }

  @Test
  public void givenTransaction_whenCreate_thenTotalPriceIsBatteryPricePlusModelPrice() {
    // given
    Transaction transaction = transactionFixture.givenACompleteTransaction();
    Price expectedTotalPrice =
        transaction
            .getVehicle()
            .getModel()
            .getPrice()
            .add(transaction.getVehicle().getBattery().getPrice());

    // when
    TransactionLogEntry transactionLogEntry = transactionLogFactory.create(transaction);

    // then
    assertThat(transactionLogEntry.getTotalPrice()).isEqualTo(expectedTotalPrice);
  }

  @Test
  public void givenTransaction_whenCreate_thenLogHasRightModelName() {
    // given
    Transaction transaction = transactionFixture.givenACompleteTransaction();
    String expectedModel = transaction.getVehicle().getModel().getName();

    // when
    TransactionLogEntry transactionLogEntry = transactionLogFactory.create(transaction);

    // then
    assertThat(transactionLogEntry.getVehicleModel()).isEqualTo(expectedModel);
  }

  @Test
  public void givenTransaction_whenCreate_thenLogHasRightBatteryType() {
    // given
    Transaction transaction = transactionFixture.givenACompleteTransaction();
    String expectedBattery = transaction.getVehicle().getBattery().getType();

    // when
    TransactionLogEntry transactionLogEntry = transactionLogFactory.create(transaction);

    // then
    assertThat(transactionLogEntry.getBatteryType()).isEqualTo(expectedBattery);
  }
}
