package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;

public class RegisterUserDtoAssembler {

  public RegisterUserDto assemble(RegisterUserRequest request) {
    var registerUserDto = new RegisterUserDto();
    registerUserDto.name = request.getName();
    registerUserDto.birthDate = request.getBirthDate();
    registerUserDto.email = request.getEmail();
    registerUserDto.password = request.getPassword();
    registerUserDto.sex = request.getSex();
    return registerUserDto;
  }
}
