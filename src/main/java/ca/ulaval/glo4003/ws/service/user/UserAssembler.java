package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.BirthDateValidator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.exception.InvalidDateFormatException;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;

import java.time.LocalDate;

public class UserAssembler {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final DateParser dateParser;
  private final BirthDateValidator birthDateValidator;

  public UserAssembler(DateParser dateParser) {
    this(dateParser, serviceLocator.resolve(BirthDateValidator.class));
  }

  public UserAssembler(DateParser dateParser, BirthDateValidator birthDateValidator) {
    this.dateParser = dateParser;
    this.birthDateValidator = birthDateValidator;
  }

  public User assemble(RegisterUserDto registerUserDto) {
    try {
      birthDateValidator.validate(registerUserDto.getBirthDate());
      LocalDate localBirthDate = dateParser.parse(registerUserDto.getBirthDate());
      BirthDate birthDate = new BirthDate(localBirthDate);
      User user = new User(
          registerUserDto.getName(),
          birthDate,
          registerUserDto.getSex(),
          registerUserDto.getEmail());
      user.addRole(Role.CUSTOMER);
      return user;
    } catch (InvalidDateFormatException invalidDateFormatException) {
      throw new InvalidFormatException(invalidDateFormatException.getDescription());
    }
  }
}
