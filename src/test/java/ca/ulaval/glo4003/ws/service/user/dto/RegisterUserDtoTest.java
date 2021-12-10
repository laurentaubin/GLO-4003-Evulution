package ca.ulaval.glo4003.ws.service.user.dto;

import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import ca.ulaval.glo4003.ws.fixture.RegisterUserRequestFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

class RegisterUserRequestTest {

  public Validator validator;

  @BeforeEach
  public void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  public void givenEmptyName_whenValidate_thenValidatorHasConstraintViolationForFieldName() {
    // given
    String expectedViolatedField = "name";
    RegisterUserRequest request = new RegisterUserRequestFixture().withName("").build();

    // when
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations =
        validator.validate(request);
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
    RegisterUserRequest request = new RegisterUserRequestFixture().withEmail("").build();

    // when
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations =
        validator.validate(request);
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
    RegisterUserRequest request = new RegisterUserRequestFixture().withPassword("").build();

    // when
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations =
        validator.validate(request);
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
    RegisterUserRequest request = new RegisterUserRequestFixture().withSex("").build();

    // when
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations =
        validator.validate(request);
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
    RegisterUserRequest request = new RegisterUserRequestFixture().withBirthDate("").build();

    // when
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations =
        validator.validate(request);
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
    RegisterUserRequest request =
        new RegisterUserRequestFixture().withEmail("bad email format").build();

    // when
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations =
        validator.validate(request);
    String actualViolatedField =
        constraintViolations.iterator().next().getPropertyPath().toString();

    // then
    assertThat(constraintViolations).hasSize(1);
    assertThat(actualViolatedField).matches(expectedViolatedField);
  }
}
