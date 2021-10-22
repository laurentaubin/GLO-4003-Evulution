package ca.ulaval.glo4003.ws.domain.shared;

import java.util.Random;

public class RandomProvider {
  private final Random random;

  public RandomProvider(Random random) {
    this.random = random;
  }

  public boolean nextBoolean() {
    return random.nextBoolean();
  }
}
