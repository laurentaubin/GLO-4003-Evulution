package ca.ulaval.glo4003.ws.domain.transaction;

import java.util.Objects;
import java.util.UUID;

public class TransactionId {

  private String id;

  public TransactionId() {
    this.id = UUID.randomUUID().toString();
  }

  public TransactionId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionId that = (TransactionId) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return id;
  }

  public static TransactionId fromString(String id) {
    return new TransactionId(id);
  }
}
