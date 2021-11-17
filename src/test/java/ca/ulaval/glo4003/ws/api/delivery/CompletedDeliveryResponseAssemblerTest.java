package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class CompletedDeliveryResponseAssemblerTest {
  private CompletedDeliveryResponseAssembler completedDeliveryResponseAssembler;

  @BeforeEach
  void setUp() {
    completedDeliveryResponseAssembler = new CompletedDeliveryResponseAssembler();
  }

  @Test
  void givenHardcodedValues_whenAssemble_thenReturnCorrectResponse() {
    // given
    int expectedPaymentTaken = 200;
    int expectedPaymentsLeft = 83;

    // when
    CompletedDeliveryResponse actual =
        completedDeliveryResponseAssembler.assemble(expectedPaymentTaken, expectedPaymentsLeft);

    // then
    assertThat(actual.paymentsLeft).isEqualTo(expectedPaymentsLeft);
    assertThat(actual.paymentTaken).isEqualTo(expectedPaymentTaken);
  }
}
