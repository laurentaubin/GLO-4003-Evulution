package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.domain.shared.DateParser;
import ca.ulaval.glo4003.ws.domain.user.BirthDate;
import ca.ulaval.glo4003.ws.domain.user.BirthDateValidator;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.testUtil.RegisterUserDtoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAssemblerTest {
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
    RegisterUserDto aUserDto = new RegisterUserDtoBuilder().build();
    BirthDate expectedBirthDate = new BirthDate(A_DATE);
    given(dateParser.parse(aUserDto.getBirthDate())).willReturn(A_DATE);

    // when
    User actualUser = userAssembler.assemble(aUserDto);

    // then
    assertThat(actualUser.getName()).matches(aUserDto.getName());
    assertThat(actualUser.getEmail()).matches(aUserDto.getEmail());
    assertThat(actualUser.getSex()).matches(aUserDto.getSex());
    assertThat(actualUser.getBirthDate()).isEqualTo(expectedBirthDate);
  }
}
