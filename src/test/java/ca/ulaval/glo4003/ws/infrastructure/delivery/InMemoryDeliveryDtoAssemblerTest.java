package ca.ulaval.glo4003.ws.infrastructure.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryMode;
import ca.ulaval.glo4003.ws.domain.delivery.Location;
import org.junit.jupiter.api.Assertions;
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
    Assertions.assertEquals(originalDelivery.getDeliveryId(), assembledDto.getDeliveryId());
    Assertions.assertEquals(
        originalDelivery.getDeliveryLocation(), assembledDto.getDeliveryLocation());
  }

  @Test
  void givenValidDeliveryDto_whenAssemblingRepoDelivery_thenReturnDomainDeliveryWithSameFields() {
    // given
    InMemoryDeliveryDto originalRepoDelivery = givenAValidDeliveryDto();

    // when
    Delivery assembledDomainDelivery = assembler.assemble(originalRepoDelivery);

    // then
    Assertions.assertEquals(
        originalRepoDelivery.getDeliveryId(), assembledDomainDelivery.getDeliveryId());
    Assertions.assertEquals(
        originalRepoDelivery.getDeliveryLocation(), assembledDomainDelivery.getDeliveryLocation());
  }

  @Test
  void
      givenValidDelivery_whenAssemblingRepoDtoAndReassemblingDelivery_thenReturnIdenticalDelivery() {
    // given
    Delivery originalDelivery = givenAValidDelivery();

    // when
    InMemoryDeliveryDto assembledDto = assembler.assemble(originalDelivery);
    Delivery reassembledDomainDelivery = assembler.assemble(assembledDto);

    // then
    Assertions.assertEquals(
        originalDelivery.getDeliveryId(), reassembledDomainDelivery.getDeliveryId());
    Assertions.assertEquals(
        originalDelivery.getDeliveryLocation(), reassembledDomainDelivery.getDeliveryLocation());
  }

  @Test
  void
      givenValidDeliveryDto_whenAssemblingDomainDeliveryAndReassemblingRepoDelivery_thenReturnIdenticalDeliveryDto() {
    // given
    InMemoryDeliveryDto originalRepoDelivery = givenAValidDeliveryDto();

    // when
    Delivery assembledDomainDelivery = assembler.assemble(originalRepoDelivery);
    InMemoryDeliveryDto reassembledRepoDelivery = assembler.assemble(assembledDomainDelivery);

    // then
    Assertions.assertEquals(
        originalRepoDelivery.getDeliveryId(), reassembledRepoDelivery.getDeliveryId());
    Assertions.assertEquals(
        originalRepoDelivery.getDeliveryLocation(), reassembledRepoDelivery.getDeliveryLocation());
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
