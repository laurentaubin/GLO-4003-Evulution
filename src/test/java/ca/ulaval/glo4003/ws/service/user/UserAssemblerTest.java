package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.BirthDateValidator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.exception.InvalidDateFormatException;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAssemblerTest {
  private static final String A_FIELD = "A FIELD";
  private static final LocalDate A_DATE = LocalDate.of(1789, 4, 30);

  @Mock private DateParser dateParser;
  @Mock private BirthDateValidator birthDateValidator;

  private UserAssembler userAssembler;

  @BeforeEach
  public void setUp() {
    userAssembler = new UserAssembler(dateParser, birthDateValidator);
  }

  @Test
  public void givenUserDto_whenAssemble_thenReturnCorrespondingUser() {
    // given
    RegisterUserDto registerUserDto = createRegisterUserDto();
    BirthDate expectedBirthDate = new BirthDate(A_DATE);
    given(dateParser.parse(registerUserDto.getBirthDate())).willReturn(A_DATE);

    // when
    User actualUser = userAssembler.assemble(registerUserDto);

    // then
    assertThat(actualUser.getName()).matches(registerUserDto.getName());
    assertThat(actualUser.getEmail()).matches(registerUserDto.getEmail());
    assertThat(actualUser.getSex()).matches(registerUserDto.getSex());
    assertThat(actualUser.getBirthDate()).isEqualTo(expectedBirthDate);
  }

  @Test
  public void givenInvalidBirthdate_whenAssemble_thenThrowsInvalidDateFormatException() {
    // given
    RegisterUserDto registerUserDto = createRegisterUserDto();
    given(dateParser.parse(registerUserDto.getBirthDate()))
        .willThrow(InvalidDateFormatException.class);

    // when
    Executable action = () -> userAssembler.assemble(registerUserDto);

    // then
    assertThrows(InvalidFormatException.class, action);
  }

  private RegisterUserDto createRegisterUserDto() {
    return new RegisterUserDto(A_FIELD, A_FIELD, A_FIELD, A_FIELD, A_FIELD);
  }

  @Test public void whenAssembleUser_thenUserHasCustomerRole() {
    // given
    RegisterUserDto registerUserDto = createRegisterUserDto();

    // when
    User user = userAssembler.assemble(registerUserDto);

    // then
    assertThat(user.getRoles()).containsExactly(Role.CUSTOMER);
  }
}
