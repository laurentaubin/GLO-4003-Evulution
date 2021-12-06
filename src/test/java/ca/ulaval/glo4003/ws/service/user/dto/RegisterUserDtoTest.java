package ca.ulaval.glo4003.ws.service.user.dto;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.testUtil.RegisterUserDtoBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterUserDtoTest {

  public Validator validator;

  @BeforeEach
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  public void givenEmptyName_whenValidate_thenValidatorHasConstraintViolationForFieldName() {
    // given
    String expectedViolatedField = "name";
    RegisterUserDto registerUserDto = new RegisterUserDtoBuilder().withName("").build();

    // when
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        validator.validate(registerUserDto);
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
    RegisterUserDto registerUserDto = new RegisterUserDtoBuilder().withEmail("").build();

    // when
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        validator.validate(registerUserDto);
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
    RegisterUserDto registerUserDto = new RegisterUserDtoBuilder().withPassword("").build();

    // when
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        validator.validate(registerUserDto);
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
    RegisterUserDto registerUserDto = new RegisterUserDtoBuilder().withSex("").build();

    // when
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        validator.validate(registerUserDto);
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
    RegisterUserDto registerUserDto = new RegisterUserDtoBuilder().withBirthDate("").build();

    // when
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        validator.validate(registerUserDto);
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
    RegisterUserDto registerUserDto =
        new RegisterUserDtoBuilder().withEmail("bad email format").build();

    // when
    Set<ConstraintViolation<RegisterUserDto>> constraintViolations =
        validator.validate(registerUserDto);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }
}
