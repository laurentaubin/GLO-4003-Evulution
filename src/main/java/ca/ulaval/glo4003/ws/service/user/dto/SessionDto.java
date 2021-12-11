package ca.ulaval.glo4003.ws.service.user.dto;

public class SessionDto {

  private final String token;

  public SessionDto(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
