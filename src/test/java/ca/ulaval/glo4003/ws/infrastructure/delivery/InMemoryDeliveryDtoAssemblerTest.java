package ca.ulaval.glo4003.ws.infrastructure.delivery;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryMode;
import ca.ulaval.glo4003.ws.domain.delivery.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryDeliveryDtoAssemblerTest {
  private static final DeliveryDestination A_DELIVERY_LOCATION =
      new DeliveryDestination(DeliveryMode.CAMPUS, Location.VACHON);
  private static final DeliveryId AN_ID = new DeliveryId("1234");

  private InMemoryDeliveryDtoAssembler assembler;

  @BeforeEach
  void setUp() {
    assembler = new InMemoryDeliveryDtoAssembler();
  }

  @Test
  void givenValidDelivery_whenAssemblingRepoDelivery_thenReturnRepoDeliveryWithSameFields() {
    // given
    Delivery originalDelivery = givenAValidDelivery();

    // when
    InMemoryDeliveryDto assembledDto = assembler.assemble(originalDelivery);

    // then
    assertThat(assembledDto.getDeliveryId()).isEqualTo(originalDelivery.getDeliveryId());
    assertThat(assembledDto.getDeliveryLocation())
        .isEqualTo(originalDelivery.getDeliveryLocation());
  }

  @Test
  void givenValidDeliveryDto_whenAssemblingRepoDelivery_thenReturnDomainDeliveryWithSameFields() {
    // given
    InMemoryDeliveryDto originalRepoDelivery = givenAValidDeliveryDto();

    // when
    Delivery assembledDomainDelivery = assembler.assemble(originalRepoDelivery);

    // then
    assertThat(assembledDomainDelivery.getDeliveryId())
        .isEqualTo(originalRepoDelivery.getDeliveryId());
    assertThat(assembledDomainDelivery.getDeliveryLocation())
        .isEqualTo(originalRepoDelivery.getDeliveryLocation());
  }

  private Delivery givenAValidDelivery() {
    Delivery delivery = new Delivery();
    delivery.setDeliveryId(AN_ID);
    delivery.setDeliveryLocation(A_DELIVERY_LOCATION);
    return delivery;
  }

  private InMemoryDeliveryDto givenAValidDeliveryDto() {
    return new InMemoryDeliveryDto(AN_ID, A_DELIVERY_LOCATION);
  }
}
