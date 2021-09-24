package ca.ulaval.glo4003.ws.api.customer.dto;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.testUtil.RegisterCustomerDtoBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterCustomerDtoTest {

  public Validator validator;

  @BeforeEach
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  public void givenEmptyName_whenValidate_thenValidatorHasConstraintViolationForFieldName() {
    // given
    String expectedViolatedField = "name";
    RegisterCustomerDto registerCustomerDto = new RegisterCustomerDtoBuilder().withName("").build();

    // when
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        validator.validate(registerCustomerDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }

  @Test
  public void givenEmptyEmail_whenValidate_thenValidatorHasOneConstraintViolation() {
    // given
    String expectedViolatedField = "email";
    RegisterCustomerDto registerCustomerDto =
        new RegisterCustomerDtoBuilder().withEmail("").build();

    // when
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        validator.validate(registerCustomerDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }

  @Test
  public void givenEmptyPassword_whenValidate_thenValidatorHasOneConstraintViolation() {
    // given
    String expectedViolatedField = "password";
    RegisterCustomerDto registerCustomerDto =
        new RegisterCustomerDtoBuilder().withPassword("").build();

    // when
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        validator.validate(registerCustomerDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }

  @Test
  public void givenEmptySex_whenValidate_thenValidatorHasOneConstraintViolation() {
    // given
    String expectedViolatedField = "sex";
    RegisterCustomerDto registerCustomerDto = new RegisterCustomerDtoBuilder().withSex("").build();

    // when
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        validator.validate(registerCustomerDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }

  @Test
  public void givenEmptyBirthDate_whenValidate_thenValidatorHasOneConstraintViolation() {
    // given
    String expectedViolatedField = "birthDate";
    RegisterCustomerDto registerCustomerDto =
        new RegisterCustomerDtoBuilder().withBirthDate("").build();

    // when
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        validator.validate(registerCustomerDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }

  @Test
  public void givenEmailWithBadFormat_whenValidate_thenValidatorHasOneConstraintViolation() {
    // given
    String expectedViolatedField = "email";
    RegisterCustomerDto registerCustomerDto =
        new RegisterCustomerDtoBuilder().withEmail("bad email format").build();

    // when
    Set<ConstraintViolation<RegisterCustomerDto>> constraintViolations =
        validator.validate(registerCustomerDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }
}
