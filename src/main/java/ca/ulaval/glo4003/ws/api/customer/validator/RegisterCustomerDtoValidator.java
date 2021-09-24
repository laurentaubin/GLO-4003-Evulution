package ca.ulaval.glo4003.ws.api.customer.validator;

import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.exception.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

public class RegisterCustomerDtoValidator {

  private final Validator customerDtoValidator;
  private final DateFormatValidator dateFormatValidator;

  public RegisterCustomerDtoValidator(
      Validator fieldValidator, DateFormatValidator dateFormatValidator) {
    this.customerDtoValidator = fieldValidator;
    this.dateFormatValidator = dateFormatValidator;
  }

  public void validateDto(RegisterCustomerDto registerCustomerDto) {
    validateDtoIsNotNull(registerCustomerDto);
    validateFields(registerCustomerDto);
    dateFormatValidator.validateFormat(registerCustomerDto.getBirthDate());
  }

  private void validateDtoIsNotNull(RegisterCustomerDto registerCustomerDto) {
    if (registerCustomerDto == null) {
      throw new InvalidFormatException("The body cannot be empty");
    }
  }

  private void validateFields(RegisterCustomerDto registerCustomerDto) {
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        customerDtoValidator.validate(registerCustomerDto);

    if (!constraintViolations.isEmpty()) {
      ConstraintViolation<RegisterCustomerDto> constraint = constraintViolations.iterator().next();
      String description =
          String.format("%s %s", constraint.getPropertyPath(), constraint.getMessage());
      throw new InvalidFormatException(description);
    }
  }
}
