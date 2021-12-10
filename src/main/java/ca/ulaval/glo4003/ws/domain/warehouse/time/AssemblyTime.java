package ca.ulaval.glo4003.ws.domain.warehouse.time;

import ca.ulaval.glo4003.ws.domain.warehouse.exception.InvalidAssemblyTimeOperationException;
import java.time.Period;

public class AssemblyTime {
  final Period assemblyTime;

  public AssemblyTime(int weeks) {
    this.assemblyTime = Period.ofWeeks(weeks);
  }

  public AssemblyTime(AssemblyTime assemblyTime) {
    this.assemblyTime = Period.ofWeeks(assemblyTime.inWeeks());
  }

  public int inWeeks() {
    return assemblyTime.getDays() / 7;
  }

  public AssemblyTime subtractWeeks(int weeks) {
    int newAssemblyTime = (assemblyTime.getDays() / 7) - weeks;
    if (newAssemblyTime < 0) {
      throw new InvalidAssemblyTimeOperationException("Assembly time cannot be negative.");
    }
    return new AssemblyTime(newAssemblyTime);
  }

  public boolean isOver() {
    return assemblyTime.isZero();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AssemblyTime)) {
      return false;
    }
    AssemblyTime object = (AssemblyTime) o;
    return assemblyTime.equals(object.assemblyTime);
  }

  public AssemblyTime subtract(AssemblyTime other) {
    int newAssemblyTime = (this.assemblyTime.getDays() - other.assemblyTime.getDays()) / 7;
    if (newAssemblyTime < 0) {
      throw new InvalidAssemblyTimeOperationException("Production time cannot be negative.");
    }
    return new AssemblyTime(newAssemblyTime);
  }

  public AssemblyTime add(AssemblyTime other) {
    int newAssemblyTime = (this.assemblyTime.getDays() + other.assemblyTime.getDays()) / 7;
    return new AssemblyTime(newAssemblyTime);
  }
}
