package ca.ulaval.glo4003.ws.domain.transaction;

public class Vehicle {

  private Model model;
  private Color color;

  public Vehicle(Model model, Color color) {
    this.model = model;
    this.color = color;
  }

  public Model getModel() {
    return model;
  }

  public Color getColor() {
    return color;
  }
}
