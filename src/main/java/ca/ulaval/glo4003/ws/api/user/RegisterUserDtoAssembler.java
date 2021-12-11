package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;

public class RegisterUserDtoAssembler {

  public RegisterUserDto assemble(RegisterUserRequest request) {
    return new RegisterUserDto(
        request.getName(),
        request.getBirthDate(),
        request.getSex(),
        request.getEmail(),
        request.getPassword());
  }
}
