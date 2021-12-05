package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import ca.ulaval.glo4003.ws.api.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.User;
import java.time.LocalDate;

public class UserAssembler {
  private final DateParser dateParser;

  public UserAssembler(DateParser dateParser) {
    this.dateParser = dateParser;
  }

  public User assemble(RegisterUserDto registerUserDto) {
    LocalDate localBirthDate = dateParser.parse(registerUserDto.getBirthDate());
    BirthDate birthDate = new BirthDate(localBirthDate);

    return new User(
        registerUserDto.getName(),
        birthDate,
        registerUserDto.getSex(),
        registerUserDto.getEmail(),
        registerUserDto.getPassword());
  }
}
