package ca.ulaval.glo4003.ws.api.user.validator;

import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

public class RegisterUserDtoValidator {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final Validator userDtoValidator;
  private final BirthDateValidator birthDateValidator;

  public RegisterUserDtoValidator() {
    this(
        Validation.buildDefaultValidatorFactory().getValidator(),
        serviceLocator.resolve(BirthDateValidator.class));
  }

  public RegisterUserDtoValidator(Validator fieldValidator, BirthDateValidator birthDateValidator) {
    this.userDtoValidator = fieldValidator;
    this.birthDateValidator = birthDateValidator;
  }

  public void validateDto(RegisterUserDto registerUserDto) {
    validateDtoIsNotNull(registerUserDto);
    validateFields(registerUserDto);
    birthDateValidator.validate(registerUserDto.getBirthDate());
  }

  private void validateDtoIsNotNull(RegisterUserDto registerUserDto) {
    if (registerUserDto == null) {
      throw new InvalidFormatException("The body cannot be empty");
    }
  }

  private void validateFields(RegisterUserDto registerUserDto) {
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        userDtoValidator.validate(registerUserDto);

    if (!constraintViolations.isEmpty()) {
      ConstraintViolation<RegisterUserDto> constraint = constraintViolations.iterator().next();
      String description =
          String.format("%s %s", constraint.getPropertyPath(), constraint.getMessage());
      throw new InvalidFormatException(description);
    }
  }
}
