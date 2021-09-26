package ca.ulaval.glo4003.ws.domain.transaction;

public enum Model {
  VANDRY("Vandry"),
  POULIOT("Pouliot"),
  DESJARDINS("Desjardins");

  private String model;

  Model(String model) {
    this.model = model;
  }

  public static Model fromString(String value) {
    try {
      return Model.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidModelException();
    }
  }

  public String getModel() {
    return model;
  }
}
