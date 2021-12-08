package ca.ulaval.glo4003.ws.domain.transaction.payment;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceTest {
  private static final Price ANOTHER_PRICE = new Price(500);
  private static final BigDecimal A_BIG_DECIMAL = new BigDecimal("200.1234");
  private static final Price A_DECIMAL_PRICE = new Price(A_BIG_DECIMAL);
  private static final double A_DIVISION_FACTOR = 2.0;

  private Price priceToTest;

  @BeforeEach
  void setUp() {
    this.priceToTest = new Price(2000);
  }

  @Test
  void givenPrice_whenSubtractPrice_thenReturnNewPriceSubtractedFrom() {
    // given
    Price expectedNewPrice = new Price(1500);

    // when
    Price resultingPrice = priceToTest.subtract(ANOTHER_PRICE);

    //
    assertThat(resultingPrice).isEqualTo(expectedNewPrice);
  }

  @Test
  void givenPrice_whenDivideByFactor_thenReturnNewPriceDividedFrom() {
    // given
    Price expectedNewPrice = new Price(1000);

    // when
    Price resultingPrice = priceToTest.divide(A_DIVISION_FACTOR);

    // then
    assertThat(resultingPrice).isEqualTo(expectedNewPrice);
  }

  @Test
  void givenPrice_whenAddPrice_thenReturnNewPriceAddedTo() {
    // given
    Price expectedNewPrice = new Price(2500);

    // when
    Price resultingPrice = priceToTest.add(ANOTHER_PRICE);

    // then
    assertThat(resultingPrice).isEqualTo(expectedNewPrice);
  }

  @Test
  void givenPriceWithDecimal_whenToInt_thenReturnPriceAsInt() {
    // given
    Integer expectedPrice = 200;

    // when
    Integer resultingPrice = A_DECIMAL_PRICE.toInt();

    // then
    assertThat(resultingPrice).isEqualTo(expectedPrice);
  }

  @Test
  void givenPriceWithDecimal_whenToDouble_thenReturnPriceAsDouble() {
    // given
    double expectedPrice = 200.1234;

    // when
    double resultingPrice = A_DECIMAL_PRICE.toDouble();

    // then
    assertThat(resultingPrice).isEqualTo(expectedPrice);
  }

  @Test
  void givenTwoPricesWithSameValue_whenIsEqual_thenReturnTrue() {
    // given
    Price aPrice = new Price(A_BIG_DECIMAL);
    Price aSecondPrice = new Price(A_BIG_DECIMAL);

    // when
    boolean isEqual = aPrice.equals(aSecondPrice);

    // then
    assertThat(isEqual).isTrue();
  }

  @Test
  public void givenOnePriceInDoubleAndOneInInteger_whenIsEqual_thenReturnTrue() {
    // given
    Price aPrice = new Price(BigDecimal.valueOf(123));
    Price anotherPrice = new Price(BigDecimal.valueOf(123.0));

    // when
    boolean isEqual = aPrice.equals(anotherPrice);

    // then
    assertThat(isEqual).isTrue();
  }
}
