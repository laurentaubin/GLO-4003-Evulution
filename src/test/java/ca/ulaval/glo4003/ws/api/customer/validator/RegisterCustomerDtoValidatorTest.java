package ca.ulaval.glo4003.ws.api.customer.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.testUtil.RegisterCustomerDtoBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterCustomerDtoValidatorTest {
  private static final Path A_FIELD = PathImpl.createPathFromString("field");
  private static final String A_MESSAGE = "a message";
  private static final String ANOTHER_MESSAGE = "another message";
  private static final String A_BIRTH_DATE = "12312312321";

  @Mock private Validator fieldValidator;

  @Mock private DateFormatValidator dateFormatValidator;

  @Mock private ConstraintViolation<RegisterCustomerDto> aConstraintViolation;

  @Mock private ConstraintViolation<RegisterCustomerDto> anotherConstraintViolation;

  private RegisterCustomerDtoValidator customerDtoValidator;

  @BeforeEach
  public void setUp() {
    customerDtoValidator = new RegisterCustomerDtoValidator(fieldValidator, dateFormatValidator);
  }

  @Test
  public void
      givenFieldValidatorFindsOneViolation_whenValidateDto_thenThrowInvalidFormatExceptionWithRightDescription() {
    // given
    RegisterCustomerDto aRegisterCustomerDto = new RegisterCustomerDtoBuilder().build();
    given(aConstraintViolation.getPropertyPath()).willReturn(A_FIELD);
    given(aConstraintViolation.getMessage()).willReturn(A_MESSAGE);
    String expectedDescription = String.format("%s %s", A_FIELD, A_MESSAGE);
    Set<ConstraintViolation<RegisterCustomerDto>> violations =
        new HashSet<>(List.of(aConstraintViolation));
    given(fieldValidator.validate(aRegisterCustomerDto)).willReturn(violations);

    // when
    Executable validatingDto = () -> customerDtoValidator.validateDto(aRegisterCustomerDto);

    // then
    InvalidFormatException validationException =
        assertThrows(InvalidFormatException.class, validatingDto);
    assertThat(validationException.getDescription()).matches(expectedDescription);
  }

  @Test
  public void
      givenFieldValidatorFindsMultipleViolations_whenValidateDto_thenThrowInvalidFormatExceptionWithFirstErrorsMessage() {
    // given
    RegisterCustomerDto aRegisterCustomerDto = new RegisterCustomerDtoBuilder().build();
    given(anotherConstraintViolation.getPropertyPath()).willReturn(A_FIELD);
    given(anotherConstraintViolation.getMessage()).willReturn(ANOTHER_MESSAGE);
    String expectedDescription = String.format("%s %s", A_FIELD, ANOTHER_MESSAGE);
    Set<ConstraintViolation<RegisterCustomerDto>> violations =
        new LinkedHashSet<>(List.of(anotherConstraintViolation, aConstraintViolation));
    given(fieldValidator.validate(aRegisterCustomerDto)).willReturn(violations);

    // when
    Executable validatingDto = () -> customerDtoValidator.validateDto(aRegisterCustomerDto);

    // then
    InvalidFormatException validationException =
        assertThrows(InvalidFormatException.class, validatingDto);
    assertThat(validationException.getDescription()).matches(expectedDescription);
  }

  @Test
  public void
      givenDateFormatValidatorThrowsInvalidFormatException_whenValidateDto_thenThrowInvalidFormatException() {
    // given
    RegisterCustomerDto aRegisterCustomerDto = new RegisterCustomerDtoBuilder().build();
    aRegisterCustomerDto.setBirthDate(A_BIRTH_DATE);
    doThrow(new InvalidFormatException(A_MESSAGE))
        .when(dateFormatValidator)
        .validateFormat(A_BIRTH_DATE);

    // when
    Executable validatingDto = () -> customerDtoValidator.validateDto(aRegisterCustomerDto);

    // then
    InvalidFormatException validationException =
        assertThrows(InvalidFormatException.class, validatingDto);
    assertThat(validationException.getDescription()).matches(A_MESSAGE);
  }

  @Test
  public void givenDtoIsNull_whenValidateDto_thenThrowInvalidFormatException() {
    // given
    String expectedDescription = "The body cannot be empty";

    // when
    Executable validatingDto = () -> customerDtoValidator.validateDto(null);

    // then
    InvalidFormatException exception = assertThrows(InvalidFormatException.class, validatingDto);
    assertThat(exception.getDescription()).matches(expectedDescription);
  }
}
