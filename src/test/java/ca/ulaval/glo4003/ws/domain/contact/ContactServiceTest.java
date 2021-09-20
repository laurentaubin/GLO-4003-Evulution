package ca.ulaval.glo4003.ws.domain.contact;

import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

import ca.ulaval.glo4003.ws.api.contact.dto.ContactDto;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

  @Mock private Contact contact;
  @Mock private ContactDto contactDto;
  @Mock private ContactRepository contactRepository;
  @Mock private ContactAssembler contactAssembler;

  private ContactService contactService;

  @BeforeEach
  public void setUp() throws Exception {
    contactService = new ContactService(contactRepository, contactAssembler);
  }

  @Test
  public void givenContactsInRepository_whenFindAllContacts_thenReturnDtos() throws Exception {
    // given
    BDDMockito.given(contactRepository.findAll()).willReturn(Lists.newArrayList(contact));
    BDDMockito.given(contactAssembler.create(contact)).willReturn(contactDto);

    // when
    List<ContactDto> contactDtos = contactService.findAllContacts();

    // then
    assertThat(contactDtos).contains(contactDto);
    Mockito.verify(contactRepository).findAll();
    Mockito.verify(contactAssembler).create(contact);
  }
}
