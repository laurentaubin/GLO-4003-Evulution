package ca.ulaval.glo4003.ws.domain.user;

import java.time.LocalDate;
import java.util.Objects;

public class BirthDate {
  private final LocalDate birthDate;

  public BirthDate(LocalDate date) {
    this.birthDate = date;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof BirthDate)) {
      return false;
    }

    BirthDate otherBirthDate = (BirthDate) other;
    return birthDate.equals(otherBirthDate.birthDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(birthDate);
  }
}
